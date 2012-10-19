package org.motechproject.ananya.referencedata.flw.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerCsvRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.flw.validators.FrontLineWorkerCsvRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FrontLineWorkerCsvService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;

    public FrontLineWorkerCsvService() {
    }

    @Autowired
    public FrontLineWorkerCsvService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
    }

    public FrontLineWorkerResponse createOrUpdate(FrontLineWorkerCsvRequest frontLineWorkerCsvRequest) {
        FrontLineWorkerResponse frontLineWorkerResponse = new FrontLineWorkerResponse();
        LocationRequest locationRequest = frontLineWorkerCsvRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerCsvRequestValidator().validate(frontLineWorkerCsvRequest, location);

        if (!FLWValidationResponse.isValid())
            return frontLineWorkerResponse.withValidationResponse(FLWValidationResponse);

        FrontLineWorker frontLineWorker = constructFrontLineWorker(frontLineWorkerCsvRequest, location);
        saveFLWToDB(frontLineWorker);
        syncService.syncFrontLineWorker(frontLineWorker.getMsisdn());
        return frontLineWorkerResponse.withCreatedOrUpdated();
    }


    public void addAllWithoutValidations(List<FrontLineWorkerCsvRequest> frontLineWorkerCsvRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        for (FrontLineWorkerCsvRequest frontLineWorkerCsvRequest : frontLineWorkerCsvRequests) {
            LocationRequest locationRequest = frontLineWorkerCsvRequest.getLocation();
            Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());

            FrontLineWorker frontLineWorkerToBeSaved = constructFrontLineWorkerForBulkImport(frontLineWorkerCsvRequest, location, frontLineWorkerCsvRequests);

            frontLineWorkers.add(frontLineWorkerToBeSaved);
        }
        saveAllFLWToDB(frontLineWorkers);
        syncService.syncAllFrontLineWorkers(frontLineWorkers);
    }

    @Transactional
    private void saveFLWToDB(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.createOrUpdate(frontLineWorker);
    }

    @Transactional
    private void saveAllFLWToDB(List<FrontLineWorker> frontLineWorkers) {
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
    }

    public List<FrontLineWorker> getAllByMsisdn(Long msisdn) {
        return allFrontLineWorkers.getByMsisdn(msisdn);
    }

    private FrontLineWorker constructFrontLineWorker(FrontLineWorkerCsvRequest frontLineWorkerCsvRequest, Location location) {
        List<FrontLineWorker> frontLineWorkers = existingFLW(frontLineWorkerCsvRequest);
        if (frontLineWorkers.size() != 1) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerCsvRequest, location);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkers.get(0), frontLineWorkerCsvRequest, location);
    }

    private FrontLineWorker constructFrontLineWorkerForBulkImport(FrontLineWorkerCsvRequest frontLineWorkerRequest, Location location, List<FrontLineWorkerCsvRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkersWithSameMsisdn = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkersWithSameMsisdn.size() != 1 || hasDuplicatesInCSV(frontLineWorkerRequest, frontLineWorkerRequests)) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkersWithSameMsisdn.get(0), frontLineWorkerRequest, location);

    }

    private boolean hasDuplicatesInCSV(FrontLineWorkerCsvRequest frontLineWorkerRequest, List<FrontLineWorkerCsvRequest> frontLineWorkerRequests) {
        return CollectionUtils.cardinality(frontLineWorkerRequest, frontLineWorkerRequests) != 1;
    }


    private List<FrontLineWorker> existingFLW(FrontLineWorkerCsvRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? Collections.<FrontLineWorker>emptyList() : allFrontLineWorkers.getByMsisdn(FrontLineWorkerMapper.formatMsisdn(msisdn));
    }

}