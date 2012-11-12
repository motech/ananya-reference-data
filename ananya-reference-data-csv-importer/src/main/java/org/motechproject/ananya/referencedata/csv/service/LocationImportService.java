package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.utils.CollectionUtils;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationImportService {
    private AllLocations allLocations;
    private FrontLineWorkerService frontLineWorkerService;
    private SyncService syncService;

    public LocationImportService() {
    }

    @Autowired
    public LocationImportService(AllLocations allLocations,
                                 FrontLineWorkerService frontLineWorkerService,
                                 SyncService syncService) {
        this.allLocations = allLocations;
        this.frontLineWorkerService = frontLineWorkerService;
        this.syncService = syncService;
    }

    @Cacheable(value = "locationSearchCache")
    public Location getFor(String district, String block, String panchayat) {
        return allLocations.getFor(district, block, panchayat);
    }

    @Transactional
    public void addAllWithoutValidations(List<LocationImportCSVRequest> locationImportCSVRequests) {
        processNewLocationRequests(locationImportCSVRequests);

        processValidAndInReviewLocationsRequests(locationImportCSVRequests);

        processInvalidatingLocationRequests(locationImportCSVRequests);
    }

    private void processNewLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) object;

                        LocationStatus status = LocationStatus.from(csvRequest.getStatus());
                        return status == LocationStatus.NEW;
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportCSVRequest CSVRequest = (LocationImportCSVRequest) input;

                        Location location = new Location(
                                CSVRequest.getDistrict(),
                                CSVRequest.getBlock(),
                                CSVRequest.getPanchayat(),
                                LocationStatus.VALID,
                                null);
                        allLocations.add(location);
                        syncService.syncLocation(location);
                    }
                }
        );
    }

    private void processValidAndInReviewLocationsRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) object;

                        LocationStatus status = LocationStatus.from(csvRequest.getStatus());
                        return status.isValidOrInReviewStatus();
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                        Location validLocationFromDb = allLocations.getFor(
                                csvRequest.getDistrict(),
                                csvRequest.getBlock(),
                                csvRequest.getPanchayat()
                        );
                        validLocationFromDb.setStatus(LocationStatus.from(csvRequest.getStatus()));
                        allLocations.update(validLocationFromDb);
                        syncService.syncLocation(validLocationFromDb);
                    }
                }
        );
    }

    private void processInvalidatingLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        LocationImportCSVRequest CSVRequest = (LocationImportCSVRequest) object;

                        LocationStatus status = LocationStatus.from(CSVRequest.getStatus());
                        return status == LocationStatus.INVALID;
                    }
                }, new Closure() {
                    @Override
                    public void execute(Object input) {
                        LocationImportCSVRequest CSVRequest = (LocationImportCSVRequest) input;

                        Location invalidLocationFromDb = allLocations.getFor(
                                CSVRequest.getDistrict(),
                                CSVRequest.getBlock(),
                                CSVRequest.getPanchayat()
                        );
                        Location validLocationFromDb = allLocations.getFor(
                                CSVRequest.getNewDistrict(),
                                CSVRequest.getNewBlock(),
                                CSVRequest.getNewPanchayat()
                        );

                        invalidLocationFromDb.setStatus(LocationStatus.from(CSVRequest.getStatus()));
                        invalidLocationFromDb.setAlternateLocation(validLocationFromDb);
                        allLocations.update(invalidLocationFromDb);

                        frontLineWorkerService.updateWithAlternateLocationForFLWsWith(invalidLocationFromDb);
                        syncService.syncLocation(invalidLocationFromDb);
                    }
                }
        );
    }
}
