package org.motechproject.ananya.referencedata.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.validators.FrontLineWorkerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FrontLineWorkerService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;

    public FrontLineWorkerService() {
    }

    @Autowired
    public FrontLineWorkerService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
    }

    @Transactional
    public FrontLineWorkerResponse createOrUpdate(FrontLineWorkerRequest frontLineWorkerRequest) {
        FrontLineWorkerResponse frontLineWorkerResponse = new FrontLineWorkerResponse();
        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validate(frontLineWorkerRequest, location);

        if (!FLWValidationResponse.isValid())
            return frontLineWorkerResponse.withValidationResponse(FLWValidationResponse);

        FrontLineWorker frontLineWorker = constructFrontLineWorker(frontLineWorkerRequest, location);
        allFrontLineWorkers.createOrUpdate(frontLineWorker);
        syncService.syncFrontLineWorker(frontLineWorker.getMsisdn());
        return frontLineWorkerResponse.withCreatedOrUpdated();
    }

    @Transactional
    public void addAllWithoutValidations(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
            Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());

            FrontLineWorker frontLineWorkerToBeSaved = constructFrontLineWorkerForBulkImport(frontLineWorkerRequest, location, frontLineWorkerRequests);

            frontLineWorkers.add(frontLineWorkerToBeSaved);
        }
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
        syncService.syncAllFrontLineWorkers(frontLineWorkers);
    }

    @Transactional
    public List<FrontLineWorker> getAllByMsisdn(Long msisdn) {
        return allFrontLineWorkers.getByMsisdn(msisdn);
    }

    private FrontLineWorker constructFrontLineWorker(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        List<FrontLineWorker> frontLineWorkers = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkers.size() != 1) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkers.get(0), frontLineWorkerRequest, location);
    }

    private FrontLineWorker constructFrontLineWorkerForBulkImport(FrontLineWorkerRequest frontLineWorkerRequest, Location location, List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkersWithSameMsisdn = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkersWithSameMsisdn.size() != 1 || hasDuplicatesInCSV(frontLineWorkerRequest, frontLineWorkerRequests)) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkersWithSameMsisdn.get(0), frontLineWorkerRequest, location);

    }

    private boolean hasDuplicatesInCSV(FrontLineWorkerRequest frontLineWorkerRequest, List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        return CollectionUtils.cardinality(frontLineWorkerRequest, frontLineWorkerRequests) != 1;
    }


    private List<FrontLineWorker> existingFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? Collections.<FrontLineWorker>emptyList() : allFrontLineWorkers.getByMsisdn(Long.valueOf(FrontLineWorkerMapper.formatMsisdn(msisdn)));
    }
}