package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LocationService {
    private AllLocations allLocations;
    private LocationValidator locationValidator;

    public LocationService() {
    }

    @Autowired
    public LocationService(AllLocations allLocations, LocationValidator locationValidator) {
        this.allLocations = allLocations;
        this.locationValidator = locationValidator;
    }

    public Location getFor(String district, String block, String panchayat) {
        return allLocations.getFor(district, block, panchayat);
    }

    @Transactional
    public LocationCreationResponse add(LocationRequest locationRequest) {
        LocationCreationResponse response = new LocationCreationResponse();

        Location location = LocationMapper.mapFrom(locationRequest);
        FLWValidationResponse flwValidationResponse = locationValidator.validate(location);
        if (flwValidationResponse.isValid()) {
            allLocations.add(location);
            return response.withCreated();
        }
        return response.withValidationResponse(flwValidationResponse);
    }

    @Transactional
    public void addAllWithoutValidations(List<LocationRequest> locationRequests) {
        Set<Location> locations = new HashSet<Location>();
        for (LocationRequest request : locationRequests) {
            Location alreadyPresentLocation = allLocations.getFor(request.getDistrict(), request.getBlock(), request.getPanchayat());
            Location location = LocationMapper.mapFrom(request);
            if (alreadyPresentLocation == null) {
                locations.add(location);
            }
        }
        allLocations.addAll(locations);
    }
}
