# RepairBro — Implementation Templates

This document contains two ready-to-use files in markdown form you can drop into an AI-agent or repository: **technical.md** and **business.md**. Each file is written as a developer-ready, actionable template—architecture, APIs, schema, workflows, runbooks, commercial model, KPIs, and rollout guidance.

---

# technical.md

> **Purpose:** Complete technical spec to implement RepairBro as a cloud-native, modular platform. Includes HLD, LLD, APIs, DB schema, simulation engine design, deployment, security, testing and operational runbooks.

## 1. Overview

**Product**: RepairBro — nationwide hardware-repair platform (ticketing, diagnostics, inventory, franchise ops, simulation).

**Primary goals**:
- Provide a centralized platform for managing multi-branch repair operations.
- Standardize diagnostics and repair workflows.
- Optimize inventory and procurement.
- Enable simulation-driven expansion decisions.

**Constraints & assumptions**:
- Initial device set: Laptops, Desktops, Drones.
- Target scale: 10 → 500 branches.
- Primary region: India (GST/tax rules later plug-inable).
- Owner background: Java/Spring engineer (server-side preference).


## 2. High-Level Architecture (HLD)

**Architecture summary** (modular-monolith start → evolve to microservices):

```
         Client (Web/TechPad/FixTrack)
                   |
               API Gateway
                   |
      Authentication & Rate-limiting
                   |
  +---------------------------------------+
  |       RepairBro Modular Platform      |
  |  (Domain modules + Internal APIs)     |
  +---------------------------------------+
  | RepairCore | DiagFlow | PartsFlow etc |
                   |
         Event Bus (Kafka/RabbitMQ)
                   |
  +---------------------------------------+
  |   PostgreSQL (OLTP)  |  Analytics DW  |
  |   Redis Cache        |  Object Store  |
  +---------------------------------------+
                   |
                 Integrations
         (Payment, SMS, Suppliers, CIAM)
```

### Key design decisions
- Start with a modular Spring Boot app: single repo, modules packaged as Spring Boot modules.
- Use domain-driven design (Repair, Inventory, Finance, User, Simulation).
- Use an event bus for eventual consistency between modules and for feeding analytics.
- Keep API Gateway + auth layer to support mobile/web unified APIs.


## 3. Module Catalogue

| Module | Responsibility | Primary APIs (summary) |
|---|---:|---|
| RepairCore | Ticket lifecycle, status, history | /tickets, /tickets/{id}/actions |
| DiagFlow | Rule-based diagnostics & checklists | /diagnostics, /diagnostics/{id}/evaluate |
| PartsFlow | Inventory, reservations, procurement | /parts, /inventory, /orders |
| FixBill | Estimates, invoicing, payments | /estimates, /invoices, /payments |
| SLAGuard | SLA monitoring, complaints, audits | /sla, /complaints |
| FranchiseHub | Franchise onboarding, royalties | /franchises, /royalties |
| InsightEngine | BI and KPIs (read-only APIs) | /metrics, /reports |
| RepairSim | Simulation & scenario runner | /simulate, /scenarios |


## 4. API Design (Representative)

### Auth
- `POST /api/v1/auth/login` — returns access token (JWT) and refresh token.
- `POST /api/v1/auth/refresh` — rotate token.

### Repair Ticketing
- `POST /api/v1/tickets` — create ticket (payload: customer, device, symptom, branchId).
- `GET /api/v1/tickets/{ticketId}` — get ticket details + timeline.
- `POST /api/v1/tickets/{ticketId}/diagnose` — append diagnostic step (technician action).
- `POST /api/v1/tickets/{ticketId}/complete` — close ticket with QA checks.

### Diagnostics
- `POST /api/v1/diagnostics/evaluate` — submit symptom/context → returns suggested steps & confidence.
- `GET /api/v1/diagnostics/library` — list canonical diagnostic flows.

### Inventory & Procurement
- `GET /api/v1/parts` — catalog listing.
- `POST /api/v1/inventory/reserve` — reserve parts for ticket.
- `POST /api/v1/procurement/order` — place order with supplier.

### Simulation
- `POST /api/v1/simulate/run` — run scenario (payload: branches, demand model, staffing).
- `GET /api/v1/simulate/{id}/result` — fetch result (profit, SLA, inventory outcomes).

> All endpoints follow HATEOAS-ish patterns. Use OpenAPI 3.0 spec; generate client SDKs.


## 5. Database Schema (LLD) — Core tables

> Use PostgreSQL with UUID PKs, timestamps, FK constraints, and branch_id scoping.

### Core tables (DDL snippet)

```sql
-- Branch
CREATE TABLE branch (
  id UUID PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(64),
  tier SMALLINT,
  created_at TIMESTAMP DEFAULT now()
);

-- Customer
CREATE TABLE customer (
  id UUID PRIMARY KEY,
  name VARCHAR(128),
  phone VARCHAR(20),
  email VARCHAR(128),
  created_at TIMESTAMP DEFAULT now()
);

-- Repair ticket
CREATE TABLE repair_ticket (
  id UUID PRIMARY KEY,
  branch_id UUID REFERENCES branch(id),
  customer_id UUID REFERENCES customer(id),
  device_type VARCHAR(30),
  device_model VARCHAR(128),
  symptom TEXT,
  status VARCHAR(30) DEFAULT 'OPEN',
  priority VARCHAR(10),
  estimated_cost NUMERIC(12,2),
  created_at TIMESTAMP DEFAULT now(),
  completed_at TIMESTAMP
);

-- Diagnostic steps
CREATE TABLE diagnosis_step (
  id UUID PRIMARY KEY,
  ticket_id UUID REFERENCES repair_ticket(id),
  step_order INT,
  name VARCHAR(255),
  result VARCHAR(64),
  performed_by UUID,
  notes TEXT,
  created_at TIMESTAMP DEFAULT now()
);

-- Parts & inventory
CREATE TABLE spare_part (
  id UUID PRIMARY KEY,
  sku VARCHAR(64) UNIQUE,
  name VARCHAR(255),
  cost_price NUMERIC(12,2),
  retail_price NUMERIC(12,2),
  is_central BOOLEAN DEFAULT false
);

CREATE TABLE branch_inventory (
  branch_id UUID REFERENCES branch(id),
  spare_part_id UUID REFERENCES spare_part(id),
  qty INT DEFAULT 0,
  PRIMARY KEY (branch_id, spare_part_id)
);

-- Invoices
CREATE TABLE invoice (
  id UUID PRIMARY KEY,
  ticket_id UUID REFERENCES repair_ticket(id),
  amount NUMERIC(12,2),
  tax NUMERIC(12,2),
  status VARCHAR(30),
  created_at TIMESTAMP DEFAULT now()
);
```


## 6. Diagnostic Rule Engine (LLD)

**Pattern:** plugin-style rules implementing a `DiagnosticRule` interface (Java). Rules are registered via Spring and evaluated in pipeline.

**Rule structure**:
```java
interface DiagnosticRule {
  boolean matches(RepairContext ctx);
  List<DiagnosticAction> evaluate(RepairContext ctx);
}
```

**Rule store**: persisted flows in `diag_flow` table. Allow authoring via AdminStudio.

**Evaluation**: run top-down, short-circuit when confidence threshold reached. Keep audit trail.


## 7. Simulation Engine Design (LLD)

**Core idea**: discrete-event simulator with pluggable demand and resource models.

**Input model**:
```json
{
  "branches": 10,
  "demandModel": {"avgPerDay": 30, "distribution": "poisson"},
  "techProfile": {"juniors": 0.6,"seniors": 0.4},
  "inventoryStrategy": "centralized"
}
```

**Internals**:
- Event queue: arrival, start-repair, parts-arrival, QA, completion.
- Resource allocator: greedy + priority for urgent tickets.
- Cost model: labor (per hour), parts cost, rent by city tier.

**Outputs**:
- Branch P&L, FTFR, MTTR, SLA breach counts, inventory stockouts.

**Implementation**:
- Java service (`SimulationRunner`) executes scenarios; store results in `simulation_result` table. Use Redis for intermediate state; schedule heavy runs async via job worker.


## 8. Integration Patterns

- **Payments**: integrate Stripe/PayU/Paytm via service adapter; idempotent webhooks.
- **SMS/Email**: Twilio/MSG91 / SES.
- **Supplier APIs**: adapter pattern for vendor portals (POST order / GET shipments).
- **CIAM**: OAuth2 with provider (Azure AD B2C / Cognito) or custom JWT.


## 9. Operational Runbooks

### Deploy
- Terraform to provision infra (EKS, RDS, S3, ElastiCache).
- GitHub Actions build → push Docker → apply Helm chart.

### On-call checklist (high-level)
- Service down: check pods, logs, restart failed pods.
- DB slow: check slow query log, scale read-replicas, increase connection pool.
- Repeated SLA breaches: verify time skew, queue backlog, technician availability.


## 10. Monitoring & Observability

- Traces: OpenTelemetry + Jaeger
- Metrics: Prometheus + Grafana
- Logs: ELK stack (ELK / OpenSearch)
- Alerts: PagerDuty / Opsgenie integration

Key alerts: high error rate, event-bus lag, inventory negative qty, invoice failures.


## 11. Security & Compliance

- Authentication: OAuth2/JWT short-lived tokens + refresh.
- Authorization: RBAC with scopes (admin, branch-manager, tech, franchisee, customer).
- Data encryption: TLS in transit. AES-256 at rest for PII.
- PCI: do not store card data; use tokenization from payment gateway.
- Audit logs for financial operations and critical state changes.


## 12. Testing Strategy

- Unit tests for business rules (JUnit + Mockito)
- Integration tests (Spring Boot test, Testcontainers for DB)
- End-to-end (Cypress for web UI)
- Load testing: k6 or Gatling on ticket creation + simulate daily peaks


## 13. CI/CD

- Branch strategy: trunk-based. Feature branches review via PRs.
- Pipeline: build -> unit tests -> security scan (Snyk/OWASP) -> container scan -> deploy to staging -> acceptance tests -> promote to prod.


## 14. Deployment Plan (MVP phases)

**MVP-1 (Pilot)**: RepairCore, FixBill, TechPad (bare), PartsFlow basic, single-branch.
**MVP-2 (Scale)**: Multi-branch support, DiagFlow basic, FranchiseHub MVP, InsightEngine minimal KPIs.
**MVP-3 (Platform)**: RepairSim, ScenarioEngine, SLAGuard, advanced analytics, supplier integrations.


## 15. Developer Deliverables & Checklist

- OpenAPI spec for all public APIs.
- DB migration scripts (Flyway).
- Docker + Helm charts.
- Postman collection.
- AdminStudio UI to author diagnostics & cases.
- Simulation CLI to run scenario JSON.


## 16. Appendix — Sample Java Entities & DTOs

(Provide one sample entity and controller skeleton)

```java
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

  private final TicketService ticketService;

  @PostMapping
  public ResponseEntity<TicketDTO> create(@RequestBody CreateTicketCmd cmd) {
    TicketDTO dto = ticketService.create(cmd);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }
}
```

---

# business.md

> **Purpose:** Practical business playbook you can hand to prospective franchisees, investors, or feed into an AI-agent for ongoing ops decisions.

## 1. Executive Summary

**Brand**: RepairBro — India’s standardized hardware repair & franchise platform.

**Mission**: Deliver consistent, high-quality, tech-enabled repair services across India with centralized processes, training, and simulation-driven expansion.

**Value Proposition**:
- For customers: transparent pricing, standardized quality, quick turnarounds.
- For franchisees: proven operating system, supplier network, training, and demand through central marketing.


## 2. Business Model & Revenue Streams

| Stream | Description | Revenue owner |
|---|---:|---|
| Repair Labor | Charge per job (service fee) | Franchisee % (retains majority) |
| Parts Sales | Markup on parts sold | Shared (e.g., central procurement margin) |
| Franchise Fee | One-time onboarding fee | RepairBro HQ |
| Royalty | % of gross sales monthly | RepairBro HQ |
| AMC / CarePlus | Recurring subscription for device care | Split between HQ & franchisee |
| Training & Certification | Paid courses/certs for techs | HQ |

**Suggested initial split (example)**:
- Franchisee keeps 70–80% of labor + parts margins after fixed royalty.
- RepairBro HQ charges: one-time setup fee ₹2–4L (pilot market), royalty 6–8%.
- HQ provides: brand, software, central procurement channel, initial training, marketing leads.


## 3. Unit Economics (Template)

> Use this P&L template per branch to compute breakeven and IRR.

**Inputs (example placeholders)**
- Avg repairs/day = 25
- Avg ticket value = ₹1,800
- Gross margin (parts + labor) = 45%
- Rent (monthly) = ₹50,000
- Tech salaries (2 techs) = ₹80,000
- Other OPEX = ₹30,000
- Monthly royalty (7% of gross)

**P&L (monthly)**
- Revenue = 25 * 30 * 1800 = ₹13,50,000
- Gross profit = Revenue * 0.45 = ₹6,07,500
- OPEX total = Rent + Salaries + Other = ₹1,60,000
- Royalty = Revenue * 0.07 = ₹94,500
- EBITDA ≈ Gross profit - OPEX -Royalty = ₹3,52, (compute)

> Replace numbers with market validated inputs.


## 4. Franchise Model & Contracts

**Franchise tiers**
- *Standard*: Tier-2, small footprint (150–300 sq ft). Lower setup.
- *Premium*: Tier-1, 400–800 sq ft, deeper diagnostic lab, higher ticketing.

**Agreement elements**
- Setup fee and payment schedule
- Royalty & marketing fee %
- Minimum monthly targets (MRR / repairs)
- Brand usage & layout guidelines
- Data sharing & reporting frequency
- Audit clause & SLA enforcement
- Term & renewal terms


## 5. Pricing Strategy & Service Catalog

**Service categories**
- Quick-Fix (24–48 hrs) — standard labor rates
- Advanced Repair (2–7 days) — component replacement, board-level
- Premium & Priority (same-day / express)
- AMC tiers (Silver/Gold/Platinum)

**Price framing**
- Publish a price band for each common fix for transparency, keep dynamic discount capability for promotions.
- Use parts+labor model: parts at markup + labor flat fee.


## 6. SLA & Quality Policies

**Standard SLAs**
- Diagnostics & quote: within 24 hours of intake.
- Quick-Fix: <=48 hours.
- Advanced repair: <=7 days (depends on part lead time).
- FTFR target: >=75% within 6 months of launch.

**Warranty**
- Workmanship warranty: 30–90 days depending on repair category.
- Part warranty: as per supplier (pass-through)

**Remediation**
- If SLA breached: compensation (discount/priority for next job) or refund policy for specific categories.


## 7. Training, Certification & Talent

**Tech role ladder**
- Junior Tech (entry) — basic fault triage
- Certified Tech — board-level & advanced diagnostics
- Specialist — drone / advanced board repair

**Training program**
- 2-week onboarding: Safety, SOP, diagnostics engine use, customer handling
- Monthly microlearning + assessments via FixLibrary
- Certification tied to pay band & franchise incentive


## 8. Supplier & Procurement Playbook

**Model**
- HQ negotiates central contracts with 2–3 national suppliers for common SKUs.
- Local sourcing supported for rare parts.
- EDI/API-based purchase orders preferred, fallback to emailed PO.

**SLAs with suppliers**
- Standard: 48–72 hrs for common parts
- Emergency: next-day for priority parts (premium price)

**Buffer policy**
- Min safety stock per branch for top-50 SKUs; central warehouse for slow-moving/expensive SKU.


## 9. GTM & Marketing

**Pilot approach**
- Launch 1 owned pilot shop in Tier-2 city for 3–6 months.
- Validate ticket volumes, FTFR, supplier flows.

**Channels**
- Local SEO + Google My Business
- Branch-level social & WhatsApp lead-gen
- Partnerships: local IT resellers, colleges, corporate tie-ups
- Central promotions: seasonal AMC discounts, trade-in/refurb campaigns

**Lead flow**
- Central booking (FixTrack) → branch assignment → franchisee earns
- Central leads fed to franchised stores via FranchiseHub with lead routing rules


## 10. KPIs & Dashboards

**Core KPIs**
- Revenue per branch (daily / monthly)
- First-Time Fix Rate (FTFR)
- Mean Time To Repair (MTTR)
- Inventory Turnover (days)
- SLA breach count
- Customer Satisfaction (CSAT / NPS)
- Technician Utilization

**Dashboard guidance**
- Morning digest: branches below FTFR or with high backlog highlighted.
- Finance weekly: revenue/royalty & part cost variance.


## 11. Pilot & Rollout Plan (0–12 months)

- **Month 0–2 (Preparation):** Finalize platform MVP, supplier agreements, pilot lease + shop setup, recruit lead tech.
- **Month 3–4 (Pilot launch):** Run pilot, collect data (tickets, FTFR, lead times). Iterate processes.
- **Month 5–8 (Initial roll-out):** Onboard 5–10 franchisees in same region. Centralize procurement.
- **Month 9–12 (Scale):** Open 30+ branches across 3 states; launch RepairSim for regional expansion modeling.


## 12. Legal & Compliance

- Franchise disclosures per Indian law; prepare Franchise Disclosure Document (FDD).
- GST registration & invoicing (digital receipts with HSN codes where needed).
- Data privacy: store only PII needed; build consent capture.
- Labor compliance: payroll, PF/ESIC where applicable.


## 13. Risk Matrix & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|---|---:|---:|---|
| Supplier stockouts | High | High | Multi-supplier agreements, central buffer |
| Low FTFR | Medium | High | Training, diagflow updates, QA audits |
| SLA breaches during peaks | Medium | Medium | ScenarioEngine + temp staffing |
| Franchisee underperformance | Medium | High | Performance-based onboarding, audits, support |


## 14. Financial Templates & Metrics (annex)

- Monthly P&L spreadsheet template (CSV-ready) with inputs: avg tickets/day, avg ticket value, salary, rent, royalty.
- Unit economics calculator to test scenarios (copies for Tier-1 vs Tier-3 economics).


## 15. Next Actions & Handoff Checklist (for AI Agent Implementation)

1. Import `technical.md` and `business.md` into repo at `/docs`.
2. Create an OpenAPI skeleton for RepairCore and DiagFlow.
3. Implement DB migrations (Flyway) for core tables and create sample seed data.
4. Implement SimulationRunner stub and example scenario JSONs.
5. Build minimal React UI for Ticket creation and TechPad.
6. Provision AWS sandbox infra via Terraform modules.


---

*End of files.*

If you want, I can:
- Export these as two separate markdown files and attach them, or
- Generate the OpenAPI skeleton and basic Spring Boot module stubs next.

Which do you want me to deliver now? ✅🔧

