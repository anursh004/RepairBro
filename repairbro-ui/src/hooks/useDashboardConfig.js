import { useMemo } from 'react';
import { useAuth } from './useAuth';
import {
  DASHBOARD_CONFIG,
  GROUP_LEVEL_MAP,
  GROUP_LEVEL_PRIORITY,
} from '../config/permissions.config';

/**
 * Returns the dashboard layout configuration for the current user's
 * highest-privilege group level.
 *
 * Usage:
 *   const { kpis, charts, tables, level } = useDashboardConfig();
 *
 *   {kpis.includes('revenueToday') && <KpiCard ... />}
 *   {charts.includes('revenueTrend') && <RevenueTrendChart />}
 */
export function useDashboardConfig() {
  const { user } = useAuth();

  return useMemo(() => {
    const userGroups = user?.groups || [];

    // Resolve the highest-privilege level from the user's group memberships
    let effectiveLevel = 'OPERATIONAL'; // default fallback
    for (const level of GROUP_LEVEL_PRIORITY) {
      const hasGroup = userGroups.some(
        (g) => GROUP_LEVEL_MAP[g] === level
      );
      if (hasGroup) {
        effectiveLevel = level;
        break; // first match = highest priority
      }
    }

    const config = DASHBOARD_CONFIG[effectiveLevel] || DASHBOARD_CONFIG.OPERATIONAL;

    return {
      ...config,
      level: effectiveLevel,
    };
  }, [user]);
}
