package com.repairbro.notification.service;

import java.util.Map;

/**
 * Professional HTML email templates for all notification types.
 * Each template uses inline CSS for maximum email client compatibility.
 */
public final class NotificationTemplates {

    private NotificationTemplates() {
    }

    private static final String BRAND_COLOR = "#2563eb";
    private static final String BRAND_NAME = "RepairBro";

    // ──────────────────────────────────────────────────────────
    // HEADER / FOOTER (shared across all templates)
    // ──────────────────────────────────────────────────────────
    private static final String HEADER = """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #ffffff; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
            <div style="background: %s; padding: 24px; text-align: center;">
                <h1 style="color: #ffffff; margin: 0; font-size: 24px;">🔧 %s</h1>
            </div>
            <div style="padding: 24px;">
            """
            .formatted(BRAND_COLOR, BRAND_NAME);

    private static final String FOOTER = """
            </div>
            <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #6b7280;">
                <p>RepairBro — Nationwide Hardware Repair Platform</p>
                <p>This is an automated notification. Please do not reply directly.</p>
            </div>
            </div>
            """;

    // ──────────────────────────────────────────────────────────
    // TICKET TEMPLATES
    // ──────────────────────────────────────────────────────────

    public static String ticketCreated(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #1f2937;">🎫 Repair Ticket Created</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your repair ticket has been successfully created. Here are the details:</p>
                        <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold; color: #374151;">Ticket ID</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold; color: #374151;">Device</td>
                                <td style="padding: 8px;">%s — %s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold; color: #374151;">Issue</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold; color: #374151;">Branch</td>
                                <td style="padding: 8px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; font-weight: bold; color: #374151;">Priority</td>
                                <td style="padding: 8px;"><span style="background: %s; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px;">%s</span></td>
                            </tr>
                        </table>
                        <p>We will begin diagnosing your device shortly. You'll receive updates at each stage.</p>
                        """
                        .formatted(
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("ticketId", "N/A"),
                                data.getOrDefault("deviceType", ""),
                                data.getOrDefault("deviceModel", ""),
                                data.getOrDefault("symptom", ""),
                                data.getOrDefault("branchName", ""),
                                "URGENT".equals(data.get("priority")) ? "#dc2626" : "#2563eb",
                                data.getOrDefault("priority", "NORMAL"))
                + FOOTER;
    }

    public static String ticketStatusUpdate(Map<String, String> data) {
        String statusEmoji = switch (data.getOrDefault("newStatus", "")) {
            case "DIAGNOSING" -> "🔍";
            case "IN_REPAIR" -> "🔧";
            case "WAITING_FOR_PARTS" -> "📦";
            case "QA_CHECK" -> "✅";
            case "READY_FOR_PICKUP" -> "🎉";
            case "COMPLETED" -> "✔️";
            default -> "📋";
        };

        return HEADER
                + """
                        <h2 style="color: #1f2937;">%s Ticket Status Update</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your repair ticket <strong>%s</strong> has been updated:</p>
                        <div style="background: #f0f9ff; border-left: 4px solid %s; padding: 16px; margin: 16px 0; border-radius: 0 8px 8px 0;">
                            <p style="margin: 0; font-size: 18px;"><strong>%s</strong> → <strong>%s</strong></p>
                        </div>
                        <p><strong>Device:</strong> %s<br/>
                        <strong>Notes:</strong> %s</p>
                        <p>We'll keep you updated on the progress.</p>
                        """
                        .formatted(
                                statusEmoji,
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("ticketId", ""),
                                BRAND_COLOR,
                                data.getOrDefault("oldStatus", ""),
                                data.getOrDefault("newStatus", ""),
                                data.getOrDefault("deviceModel", ""),
                                data.getOrDefault("notes", "No additional notes"))
                + FOOTER;
    }

    public static String ticketReadyForPickup(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #059669;">🎉 Your Device is Ready!</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Great news! Your repair is complete and your device is ready for pickup.</p>
                        <div style="background: #ecfdf5; border: 1px solid #a7f3d0; padding: 16px; border-radius: 8px; margin: 16px 0;">
                            <p style="margin: 0;"><strong>Ticket:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>Device:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>Branch:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>Amount Due:</strong> ₹%s</p>
                        </div>
                        <p>Please visit our branch during working hours (10 AM - 7 PM) to collect your device.</p>
                        <p>Don't forget to bring a valid photo ID.</p>
                        """
                        .formatted(
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("ticketId", ""),
                                data.getOrDefault("deviceModel", ""),
                                data.getOrDefault("branchName", ""),
                                data.getOrDefault("finalCost", "0"))
                + FOOTER;
    }

    // ──────────────────────────────────────────────────────────
    // BILLING TEMPLATES
    // ──────────────────────────────────────────────────────────

    public static String estimateReady(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #1f2937;">💰 Repair Estimate Ready</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>We've completed the diagnosis and prepared a cost estimate for your repair:</p>
                        <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold;">Labor</td>
                                <td style="padding: 8px; text-align: right;">₹%s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold;">Parts</td>
                                <td style="padding: 8px; text-align: right;">₹%s</td>
                            </tr>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 8px; font-weight: bold;">GST (18%%)</td>
                                <td style="padding: 8px; text-align: right;">₹%s</td>
                            </tr>
                            <tr style="background: #f0f9ff;">
                                <td style="padding: 8px; font-weight: bold; font-size: 16px;">Total</td>
                                <td style="padding: 8px; text-align: right; font-weight: bold; font-size: 16px; color: %s;">₹%s</td>
                            </tr>
                        </table>
                        <p><strong>Estimated time:</strong> %s days</p>
                        <p><strong>Work description:</strong> %s</p>
                        <p>Please reply to approve or reject this estimate.</p>
                        """
                        .formatted(
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("laborCost", "0"),
                                data.getOrDefault("partsCost", "0"),
                                data.getOrDefault("taxAmount", "0"),
                                BRAND_COLOR,
                                data.getOrDefault("totalEstimate", "0"),
                                data.getOrDefault("estimatedDays", "2"),
                                data.getOrDefault("workDescription", ""))
                + FOOTER;
    }

    public static String paymentConfirmation(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #059669;">✅ Payment Received</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>We've received your payment. Here's your receipt:</p>
                        <div style="background: #ecfdf5; border: 1px solid #a7f3d0; padding: 16px; border-radius: 8px; margin: 16px 0;">
                            <p><strong>Invoice:</strong> %s</p>
                            <p><strong>Amount:</strong> ₹%s</p>
                            <p><strong>Method:</strong> %s</p>
                            <p><strong>Transaction ID:</strong> %s</p>
                        </div>
                        <p>Thank you for choosing RepairBro!</p>
                        """
                        .formatted(
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("invoiceNumber", ""),
                                data.getOrDefault("amount", "0"),
                                data.getOrDefault("method", ""),
                                data.getOrDefault("transactionId", ""))
                + FOOTER;
    }

    // ──────────────────────────────────────────────────────────
    // SLA & ALERT TEMPLATES
    // ──────────────────────────────────────────────────────────

    public static String slaBreachAlert(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #dc2626;">⚠️ SLA Breach Alert</h2>
                        <p>This is an automated SLA breach notification for branch management.</p>
                        <div style="background: #fef2f2; border-left: 4px solid #dc2626; padding: 16px; margin: 16px 0; border-radius: 0 8px 8px 0;">
                            <p style="margin: 0;"><strong>Ticket:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>SLA Type:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>Branch:</strong> %s</p>
                            <p style="margin: 4px 0;"><strong>Overdue By:</strong> %s hours</p>
                        </div>
                        <p><strong>Action Required:</strong> Please review and escalate immediately.</p>
                        """
                        .formatted(
                                data.getOrDefault("ticketId", ""),
                                data.getOrDefault("slaType", ""),
                                data.getOrDefault("branchName", ""),
                                data.getOrDefault("overdueHours", ""))
                + FOOTER;
    }

    public static String welcomeUser(Map<String, String> data) {
        return HEADER + """
                <h2 style="color: #1f2937;">👋 Welcome to RepairBro!</h2>
                <p>Dear <strong>%s</strong>,</p>
                <p>Your account has been successfully created. Here's what you need to know:</p>
                <ul style="line-height: 2;">
                    <li>📱 Track your repairs in real-time</li>
                    <li>💰 Get transparent cost estimates before we start</li>
                    <li>🔧 Professional technicians across India</li>
                    <li>🛡️ 30-90 day warranty on all repairs</li>
                </ul>
                <p>Your login email: <strong>%s</strong></p>
                <p>If you have any questions, reach out to your nearest branch.</p>
                """.formatted(
                data.getOrDefault("fullName", "User"),
                data.getOrDefault("email", "")) + FOOTER;
    }

    public static String warrantyIssued(Map<String, String> data) {
        return HEADER
                + """
                        <h2 style="color: #1f2937;">🛡️ Warranty Certificate</h2>
                        <p>Dear <strong>%s</strong>,</p>
                        <p>Your repair comes with the following warranty:</p>
                        <div style="background: #eff6ff; border: 1px solid #bfdbfe; padding: 16px; border-radius: 8px; margin: 16px 0;">
                            <p><strong>Device:</strong> %s</p>
                            <p><strong>Repair:</strong> %s</p>
                            <p><strong>Warranty Period:</strong> %s days (%s)</p>
                            <p><strong>Valid Until:</strong> %s</p>
                        </div>
                        <p>If you experience the same issue within the warranty period, contact us for a free re-repair.</p>
                        """
                        .formatted(
                                data.getOrDefault("customerName", "Customer"),
                                data.getOrDefault("deviceModel", ""),
                                data.getOrDefault("repairDescription", ""),
                                data.getOrDefault("warrantyDays", "30"),
                                data.getOrDefault("warrantyCategory", "STANDARD"),
                                data.getOrDefault("expiryDate", ""))
                + FOOTER;
    }
}
