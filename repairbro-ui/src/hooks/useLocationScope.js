import { useMemo } from 'react';
import { useAuth } from './useAuth';

/**
 * Provides the user's location context for data scoping.
 *
 * Usage:
 *   const { locations, primaryLocation, isMultiLocation } = useLocationScope();
 *
 *   // Auto-scope API call
 *   ticketApi.getByBranch(primaryLocation);
 *
 *   // Show branch selector only for multi-location users
 *   {isMultiLocation && <LocationSelector />}
 */
export function useLocationScope() {
  const { user } = useAuth();

  return useMemo(() => {
    const locations = user?.locations || [];
    const primaryLocation = user?.primaryLocation || locations[0] || null;
    const isMultiLocation = locations.length > 1;

    return {
      /** All branch IDs the user has access to */
      locations,
      /** The user's primary branch ID */
      primaryLocation,
      /** True if user is assigned to more than one location */
      isMultiLocation,
      /** True if user has no location (e.g. external Customer) */
      hasNoLocation: locations.length === 0,
    };
  }, [user]);
}
