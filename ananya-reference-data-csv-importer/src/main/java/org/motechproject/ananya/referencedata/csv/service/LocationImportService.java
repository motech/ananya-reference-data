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
                hasStatus(Arrays.asList(LocationStatus.NEW)), new Closure() {
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
                hasStatus(Arrays.asList(LocationStatus.VALID, LocationStatus.IN_REVIEW)), new Closure() {
            @Override
            public void execute(Object input) {
                LocationImportCSVRequest csvRequest = (LocationImportCSVRequest) input;

                Location existingLocationInDB = allLocations.getFor(
                        csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );

                if (doesIdenticalLocationExistsInDB(csvRequest, existingLocationInDB, null)) return;

                existingLocationInDB.setStatus(LocationStatus.from(csvRequest.getStatus()));
                allLocations.update(existingLocationInDB);
                syncService.syncLocation(existingLocationInDB);
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


                Location invalidLocationFromDb = allLocations.getFor(
                        csvRequest.getDistrict(),
                        csvRequest.getBlock(),
                        csvRequest.getPanchayat()
                );
                Location validLocationFromDb = allLocations.getFor(
                        csvRequest.getNewDistrict(),
                        csvRequest.getNewBlock(),
                        csvRequest.getNewPanchayat()
                );

                if(doesIdenticalLocationExistsInDB(csvRequest, invalidLocationFromDb,validLocationFromDb)) return;

                invalidLocationFromDb.setStatus(LocationStatus.from(csvRequest.getStatus()));
                invalidLocationFromDb.setAlternateLocation(validLocationFromDb);
                allLocations.update(invalidLocationFromDb);

                frontLineWorkerService.updateWithAlternateLocationForFLWsWith(invalidLocationFromDb);
                syncService.syncLocation(invalidLocationFromDb);
            }
        }
        );
    }

    private boolean doesIdenticalLocationExistsInDB(LocationImportCSVRequest csvRequest, Location existingLocationInDb, Location alternateLocationInDb) {
        Location locationFromCSV = new Location(csvRequest.getDistrict(), csvRequest.getBlock(), csvRequest.getPanchayat(), csvRequest.getStatusEnum(), alternateLocationInDb);
        if (locationFromCSV.equals(existingLocationInDb)) return true;
        return false;
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
