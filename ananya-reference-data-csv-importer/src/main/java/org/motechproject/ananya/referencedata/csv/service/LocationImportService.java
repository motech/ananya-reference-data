package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.utils.CollectionUtils;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationImportService {
    private AllLocations allLocations;
    private FrontLineWorkerService frontLineWorkerService;

    public LocationImportService() {
    }

    @Autowired
    public LocationImportService(AllLocations allLocations,
                                 FrontLineWorkerService frontLineWorkerService) {
        this.allLocations = allLocations;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @Cacheable(value = "locationSearchCache")
    public Location getFor(String district, String block, String panchayat) {
        return allLocations.getFor(district, block, panchayat);
    }

    @Transactional
    public void addAllWithoutValidations(List<LocationImportRequest> locationImportRequests) {
        processNewLocationRequests(locationImportRequests);

        processValidatingLocationsRequests(locationImportRequests);

        processInvalidatingLocationRequests(locationImportRequests);
    }

    private void processNewLocationRequests(List<LocationImportRequest> locationImportRequests) {
        CollectionUtils.forAllDo(locationImportRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportRequest request = (LocationImportRequest) object;

                        String status = request.getStatus();
                        return LocationStatus.isNewStatus(status);
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportRequest request = (LocationImportRequest) input;

                        allLocations.add(new Location(
                                request.getDistrict(),
                                request.getBlock(),
                                request.getPanchayat(),
                                LocationStatus.VALID.name(),
                                null)
                        );
                    }
                }
        );
    }

    private void processValidatingLocationsRequests(List<LocationImportRequest> locationImportRequests) {
        CollectionUtils.forAllDo(locationImportRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportRequest request = (LocationImportRequest) object;

                        String status = request.getStatus();
                        return LocationStatus.isValidStatus(status);
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportRequest request = (LocationImportRequest) input;

                        Location validLocationFromDb = allLocations.getFor(
                                request.getDistrict(),
                                request.getBlock(),
                                request.getPanchayat()
                        );
                        validLocationFromDb.setStatus(LocationStatus.from(request.getStatus()));
                        allLocations.update(validLocationFromDb);
                    }
                }
        );
    }

    private void processInvalidatingLocationRequests(List<LocationImportRequest> locationImportRequests) {
        CollectionUtils.forAllDo(locationImportRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportRequest request = (LocationImportRequest) object;

                        return LocationStatus.isInvalidStatus(request.getStatus());
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportRequest request = (LocationImportRequest) input;

                        Location invalidLocationFromDb = allLocations.getFor(
                                request.getDistrict(),
                                request.getBlock(),
                                request.getPanchayat()
                        );
                        Location validLocationFromDb = allLocations.getFor(
                                request.getNewDistrict(),
                                request.getNewBlock(),
                                request.getNewPanchayat()
                        );

                        invalidLocationFromDb.setStatus(LocationStatus.from(request.getStatus()));
                        invalidLocationFromDb.setAlternateLocation(validLocationFromDb);
                        allLocations.update(invalidLocationFromDb);

                        frontLineWorkerService.updateWithAlternateLocationForFLWsWith(invalidLocationFromDb);
                    }
                }
        );
    }
}
