package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationService {
    private AllLocations allLocations;
    private SyncService syncService;

    public LocationService() {
    }

    @Autowired
    public LocationService(AllLocations allLocations, SyncService syncService) {
        this.allLocations = allLocations;
        this.syncService = syncService;
    }

    @Transactional
    public Location createAndFetch(LocationRequest request) {
        Location location = getExistingLocation(request);
        if (location == null) {
            location = LocationMapper.mapFrom(request);
            allLocations.add(location);
            syncService.syncLocation(location);
        }
        return alternateLocation(location);
    }

    private Location alternateLocation(Location location) {
        return location.isInvalid() ? location.getAlternateLocation() : location;
    }

    public List<Location> getAllValidLocations(String state) {
       return allLocations.getForStatusesInAGivenState(state, LocationStatus.VALID);
    }

    public List<Location> getLocationsToBeVerified() {
        return allLocations.getForStatuses(LocationStatus.NOT_VERIFIED, LocationStatus.IN_REVIEW);
    }

    private Location getExistingLocation(LocationRequest request) {
        return allLocations.getFor(request.getState(), request.getDistrict(), request.getBlock(), request.getPanchayat());
    }
    
    public List<Location> getLocationbyStatus(LocationRequest locationRequest, LocationStatus locationStatus) {
    	return allLocations.getLocationByStatus(locationRequest, locationStatus);
    }
}
