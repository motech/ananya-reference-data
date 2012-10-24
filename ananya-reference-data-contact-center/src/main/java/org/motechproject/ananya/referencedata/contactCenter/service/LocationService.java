package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private AllLocations allLocations;

    @Autowired
    public LocationService(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    private Location getExistingLocation(LocationRequest request) {
        return allLocations.getFor(request.getDistrict(), request.getBlock(), request.getPanchayat());
    }

    public Location handleLocation(LocationRequest request) {
        Location location = getExistingLocation(request);
        if (location == null) {
            location = LocationMapper.mapFrom(request);
            allLocations.add(location);
        }
        return location;
    }
}
