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

import java.util.Arrays;
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
    public Location getFor(String state, String district, String block, String panchayat) {
        return allLocations.getFor(state, district, block, panchayat);
    }

    @Transactional
    public void addAllWithoutValidations(List<LocationImportCSVRequest> locationImportCSVRequests) {
        processNewLocationRequests(locationImportCSVRequests);

        processValidAndInReviewLocationsRequests(locationImportCSVRequests);

        processInvalidatingLocationRequests(locationImportCSVRequests);
    }

    private void processNewLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.NEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location location = new Location(
                        csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat(),
                        csvRequest.getState(), LocationStatus.VALID,
                        null);
                allLocations.add(location);
                syncService.syncLocation(location);
            }
        }
        );
    }

    private void processValidAndInReviewLocationsRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.VALID, LocationStatus.IN_REVIEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location updatedLocation = allLocations.getFor(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );

                Location unchangedLocationInDb = updatedLocation.clone();
                updatedLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
                if (doesIdenticalLocationExistsInDB(unchangedLocationInDb, updatedLocation)) return;

                allLocations.update(updatedLocation);
                syncService.syncLocation(updatedLocation);
            }
        }
        );
    }

       private void processInvalidatingLocationRequests(List<LocationImportCSVRequest> locationImportCSVRequests) {
        CollectionUtils.forAllDo(locationImportCSVRequests,
                hasStatus(Arrays.asList(LocationStatus.INVALID)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;


                Location updatedInvalidLocation = allLocations.getFor(
                        csvRequest.getState(), csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );
                Location validLocationFromDb = allLocations.getFor(
                        csvRequest.getNewState(), csvRequest.getNewDistrict(),
                        csvRequest.getNewBlock(),
                        csvRequest.getNewPanchayat()
                );

                Location unchangedInvalidLocationFromDb = updatedInvalidLocation.clone();

                updatedInvalidLocation.setStatus(LocationStatus.from(csvRequest.getStatus()));
                updatedInvalidLocation.setAlternateLocation(validLocationFromDb);

                if(doesIdenticalLocationExistsInDB(unchangedInvalidLocationFromDb, updatedInvalidLocation)) return;

                allLocations.update(updatedInvalidLocation);
                frontLineWorkerService.updateWithAlternateLocationForFLWsWith(updatedInvalidLocation);
                syncService.syncLocation(updatedInvalidLocation);
            }
        }
        );
    }

    private boolean doesIdenticalLocationExistsInDB(Location locationInDb, Location updatedLocation) {
        return locationInDb.equals(updatedLocation);
    }

    private Predicate hasStatus(final List<LocationStatus> statuses) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) object;

                return statuses.contains(LocationStatus.from(csvRequest.getStatus()));
            }
        };
    }
}
