package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private AllLocations allLocations;
    private SyncService syncService;

    @Autowired
    public LocationService(AllLocations allLocations, SyncService syncService) {
        this.allLocations = allLocations;
        this.syncService = syncService;
    }

    public Location handleLocation(LocationRequest request) {
        Location location = getExistingLocation(request);
        if (location == null) {
            location = LocationMapper.mapFrom(request);
            allLocations.add(location);
            syncService.syncLocation(location);
        }
        return location;
    }

    public List<Location> getAllValidLocations() {
        return allLocations.getForStatuses(LocationStatus.VALID);
    }

    public List<Location> getLocationsToBeVerified() {
        return allLocations.getForStatuses(LocationStatus.NOT_VERIFIED, LocationStatus.IN_REVIEW);
    }

    private Location getExistingLocation(LocationRequest request) {
        return allLocations.getFor(request.getDistrict(), request.getBlock(), request.getPanchayat());
    }
}
