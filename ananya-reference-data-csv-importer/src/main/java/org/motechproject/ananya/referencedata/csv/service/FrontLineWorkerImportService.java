package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.mapper.FrontLineWorkerImportMapper;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Service
public class FrontLineWorkerImportService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;

    public FrontLineWorkerImportService() {
    }

    @Autowired
    public FrontLineWorkerImportService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
    }

    @Transactional
    public void addAllWithoutValidations(List<FrontLineWorkerImportRequest> frontLineWorkerImportRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        for (FrontLineWorkerImportRequest frontLineWorkerImportRequest : frontLineWorkerImportRequests) {
            LocationRequest locationRequest = frontLineWorkerImportRequest.getLocation();
            Location location = getExistingLocation(locationRequest);

            FrontLineWorker frontLineWorkerToBeSaved = constructFrontLineWorkerForBulkImport(frontLineWorkerImportRequest, location, frontLineWorkerImportRequests);

            frontLineWorkers.add(frontLineWorkerToBeSaved);
        }

        saveAllFLWToDB(frontLineWorkers);
        syncService.syncAllFrontLineWorkers(frontLineWorkers);
    }

    private Predicate flwWithVerificationStatus() {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                return isNotBlank(((FrontLineWorker) o).getVerificationStatus());
            }
        };
    }

    private Location getExistingLocation(LocationRequest locationRequest) {
        Location location = allLocations.getFor(locationRequest.getState(), locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        return location.isInvalid() ? location.getAlternateLocation() : location;
    }

    private void saveAllFLWToDB(List<FrontLineWorker> frontLineWorkers) {
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
    }

    public List<FrontLineWorker> getAllByMsisdn(Long msisdn) {
        return allFrontLineWorkers.getByMsisdn(msisdn);
    }

    private FrontLineWorker constructFrontLineWorkerForBulkImport(FrontLineWorkerImportRequest frontLineWorkerRequest, Location location, List<FrontLineWorkerImportRequest> frontLineWorkerRequests) {
        if (hasDuplicatesInCSV(frontLineWorkerRequest, frontLineWorkerRequests)) {
            return FrontLineWorkerImportMapper.mapToNewFlw(frontLineWorkerRequest, location);
        }
        FrontLineWorker flwToBeUpdated = flwToBeUpdated(frontLineWorkerRequest);
        if (flwToBeUpdated != null)
            return FrontLineWorkerImportMapper.mapToExistingFlw(flwToBeUpdated, frontLineWorkerRequest, location);
        return FrontLineWorkerImportMapper.mapToNewFlw(frontLineWorkerRequest, location);
    }

    private FrontLineWorker flwToBeUpdated(FrontLineWorkerImportRequest frontLineWorkerRequest) {
        List<FrontLineWorker> frontLineWorkersWithSameMsisdn = existingFLW(frontLineWorkerRequest);
        if(frontLineWorkersWithSameMsisdn.size() == 1)
            return frontLineWorkersWithSameMsisdn.get(0);
        return (FrontLineWorker) CollectionUtils.find(frontLineWorkersWithSameMsisdn, flwWithVerificationStatus());
    }

    private boolean hasDuplicatesInCSV(FrontLineWorkerImportRequest frontLineWorkerRequest, List<FrontLineWorkerImportRequest> frontLineWorkerRequests) {
        return CollectionUtils.cardinality(frontLineWorkerRequest, frontLineWorkerRequests) != 1;
    }

    private List<FrontLineWorker> existingFLW(FrontLineWorkerImportRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? new ArrayList<FrontLineWorker>() : allFrontLineWorkers.getByMsisdn(FrontLineWorkerImportMapper.formatMsisdn(msisdn));
    }
}