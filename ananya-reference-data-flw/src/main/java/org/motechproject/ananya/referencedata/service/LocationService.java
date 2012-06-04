package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.response.ValidationResponse;
import org.motechproject.ananya.referencedata.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private AllLocations allLocations;

    @Autowired
    public LocationService(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public LocationCreationResponse add(LocationRequest locationRequest) {
        LocationCreationResponse response = new LocationCreationResponse();

        Location location = LocationMapper.mapFrom(locationRequest);
        LocationList locationList = new LocationList(this.allLocations.getAll());

        ValidationResponse validationResponse = new LocationValidator(locationList).validate(location);
        if(validationResponse.isValid()) {
            location = locationList.updateLocationCode(location);
            this.allLocations.add(location);
            return response.withCreated();
        }

        return response.withValidationResponse(validationResponse);
    }
}
