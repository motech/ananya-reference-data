package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        FLWValidationResponse FLWValidationResponse = new LocationValidator(locationList).validate(location);
        if(FLWValidationResponse.isValid()) {
            location = locationList.updateLocationCode(location);
            this.allLocations.add(location);
            return response.withCreated();
        }

        return response.withValidationResponse(FLWValidationResponse);
    }

    public List<Location> getAll() {
        return allLocations.getAll();
    }

    public void addAllWithoutValidations(List<LocationRequest> locationRequests) {
        List<Location> locations = new ArrayList<Location>();
        for(LocationRequest request : locationRequests) {
            locations.add(LocationMapper.mapFrom(request));
        }
        allLocations.addAll(locations);
    }
}
