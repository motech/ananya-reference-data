package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationList;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {
    private AllLocations allLocations;

    public LocationService() {
    }

    @Autowired
    public LocationService(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public List<Location> getAll() {
        return allLocations.getAll();
    }

    @Transactional
    public LocationCreationResponse add(LocationRequest locationRequest) {
        LocationCreationResponse response = new LocationCreationResponse();

        Location location = LocationMapper.mapFrom(locationRequest);
        LocationList locationList = new LocationList(this.allLocations.getAll());

        FLWValidationResponse FLWValidationResponse = new LocationValidator(locationList).validate(location);
        if(FLWValidationResponse.isValid()) {
            allLocations.add(location);
            return response.withCreated();
        }

        return response.withValidationResponse(FLWValidationResponse);
    }

    @Transactional
    public void addAllWithoutValidations(List<LocationRequest> locationRequests) {
        LocationList locationList = new LocationList(allLocations.getAll());
        List<Location> locations = new ArrayList<Location>();
        for(LocationRequest request : locationRequests) {
            addAllLocations(locationList, locations, request);
        }
        allLocations.addAll(locations);
    }

    private void addAllLocations(LocationList locationList, List<Location> locations, LocationRequest request) {
        Location location = LocationMapper.mapFrom(request);
        if(locationList.isAlreadyPresent(location))
            return;
        locationList.add(location);
        locations.add(location);
    }
}
