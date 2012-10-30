package org.motechproject.ananya.referencedata.csv.service;

import org.motechproject.ananya.referencedata.csv.mapper.LocationImportMapper;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
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
    public void addAllWithoutValidations(List<LocationImportRequest> locationImportRequests) {
        Set<Location> locations = new HashSet<>();
        for (LocationImportRequest request : locationImportRequests) {
            Location alreadyPresentLocation = allLocations.getFor(request.getDistrict(), request.getBlock(), request.getPanchayat());
            Location locationToAdd =  alreadyPresentLocation == null
                    ? LocationImportMapper.mapFrom(request)
                    : LocationImportMapper.mapFrom(alreadyPresentLocation, request);

            locations.add(locationToAdd);
        }
        allLocations.addAll(locations);
    }
}
