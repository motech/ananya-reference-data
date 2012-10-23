package org.motechproject.ananya.referencedata.csv.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LocationImportService {
    private AllLocations allLocations;
    private LocationImportValidator locationImportValidator;

    public LocationImportService() {
    }

    @Autowired
    public LocationImportService(AllLocations allLocations, LocationImportValidator locationImportValidator) {
        this.allLocations = allLocations;
        this.locationImportValidator = locationImportValidator;
    }

    @Cacheable(value = "locationSearchCache")
    public Location getFor(String district, String block, String panchayat) {
        return allLocations.getFor(district, block, panchayat);
    }

    @Transactional
    public LocationCreationResponse add(LocationRequest locationRequest) {
        LocationCreationResponse response = new LocationCreationResponse();

        Location location = LocationMapper.mapFrom(locationRequest);
        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(location);
        if (locationValidationResponse.isValid()) {
            allLocations.add(location);
            return response.withCreated();
        }
        return response.withValidationResponse(locationValidationResponse);
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
