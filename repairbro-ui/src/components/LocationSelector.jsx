import { useState, useEffect } from 'react';
import { useLocationScope } from '../hooks/useLocationScope';
import { useApi } from '../hooks/useApi';
import { branchApi } from '../api/branches';

/**
 * LocationSelector — branch dropdown filtered to the current user's
 * assigned locations.
 *
 * - Auto-selects the user's primaryLocation on mount
 * - Single-location users see a disabled indicator (no dropdown)
 * - Multi-location users get a dropdown of their branches
 * - Super Admins/Regional Managers who have BRANCH_VIEW for all see all branches
 *
 * Usage:
 *   <LocationSelector
 *     value={selectedBranch}
 *     onChange={(branchId) => setSelectedBranch(branchId)}
 *   />
 */
export default function LocationSelector({ value, onChange, className = '' }) {
  const { locations, primaryLocation, isMultiLocation, hasNoLocation } = useLocationScope();
  const { data: allBranches } = useApi(() => branchApi.getAll());
  const [branchNames, setBranchNames] = useState({});

  // Build a map of branchId -> name from API data
  useEffect(() => {
    if (allBranches && Array.isArray(allBranches)) {
      const map = {};
      allBranches.forEach((b) => {
        map[b.id] = b.name || b.city || b.id?.slice(0, 8);
      });
      setBranchNames(map);
    }
  }, [allBranches]);

  // Auto-select primary location if no value set
  useEffect(() => {
    if (!value && primaryLocation) {
      onChange?.(primaryLocation);
    }
  }, [value, primaryLocation, onChange]);

  // No locations assigned (external user like Customer)
  if (hasNoLocation) {
    return null;
  }

  // Single location: show as a badge, not a dropdown
  if (!isMultiLocation) {
    const name = branchNames[primaryLocation] || primaryLocation?.slice(0, 8) || '--';
    return (
      <div className={`location-badge ${className}`}>
        <span className="location-dot" />
        {name}
      </div>
    );
  }

  // Multi-location: render dropdown
  return (
    <select
      className={`form-select location-selector ${className}`}
      value={value || primaryLocation || ''}
      onChange={(e) => onChange?.(e.target.value)}
    >
      {locations.map((branchId) => (
        <option key={branchId} value={branchId}>
          {branchNames[branchId] || branchId.slice(0, 8)}
        </option>
      ))}
    </select>
  );
}
