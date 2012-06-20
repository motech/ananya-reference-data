package org.motechproject.ananya.referencedata.flw.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.flw.validators.FrontLineWorkerValidator;
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
    private AnanyaReferenceDataPropertiesService propertiesService;

    public FrontLineWorkerService() {
    }

    @Autowired
    public FrontLineWorkerService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService, AnanyaReferenceDataPropertiesService propertiesService) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
        this.propertiesService = propertiesService;
    }

    public FrontLineWorkerResponse createOrUpdate(FrontLineWorkerRequest frontLineWorkerRequest) {
        FrontLineWorkerResponse frontLineWorkerResponse = new FrontLineWorkerResponse();
        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validate(frontLineWorkerRequest, location);

        if (!FLWValidationResponse.isValid()) {
            return frontLineWorkerResponse.withValidationResponse(FLWValidationResponse);
        }

        boolean shouldSync = propertiesService.isSyncOn();
        FrontLineWorker frontLineWorker = constructFrontLineWorker(frontLineWorkerRequest, location, shouldSync);
        saveFLWToDB(frontLineWorker);
        return frontLineWorkerResponse.withCreatedOrUpdated();
    }

    public void addAllWithoutValidations(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        boolean shouldSync = propertiesService.isSyncOn();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
            Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());

            FrontLineWorker frontLineWorkerToBeSaved = constructFrontLineWorkerForBulkImport(frontLineWorkerRequest, location, frontLineWorkerRequests, shouldSync);

            frontLineWorkers.add(frontLineWorkerToBeSaved);
        }
        saveAllFLWToDB(frontLineWorkers);
    }

    public List<FrontLineWorker> getAllToBeSynced() {
        return allFrontLineWorkers.getAllToBeSynced();
    }

    public List<FrontLineWorker> getAllByMsisdn(Long msisdn) {
        return allFrontLineWorkers.getByMsisdn(msisdn);
    }

    public void setSyncComplete(FrontLineWorker frontLineWorker) {
        frontLineWorker.setShouldSync(false);
        allFrontLineWorkers.createOrUpdate(frontLineWorker);
    }

    @Transactional
    private void saveFLWToDB(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.createOrUpdate(frontLineWorker);
    }

    @Transactional
    private void saveAllFLWToDB(List<FrontLineWorker> frontLineWorkers) {
        allFrontLineWorkers.createOrUpdateAll(frontLineWorkers);
    }

    private FrontLineWorker constructFrontLineWorker(FrontLineWorkerRequest frontLineWorkerRequest, Location location, boolean shouldSync) {
        List<FrontLineWorker> frontLineWorkers = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkers.size() != 1) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location, shouldSync);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkers.get(0), frontLineWorkerRequest, location, shouldSync);
    }


    private FrontLineWorker constructFrontLineWorkerForBulkImport(FrontLineWorkerRequest frontLineWorkerRequest, Location location, List<FrontLineWorkerRequest> frontLineWorkerRequests, boolean shouldSync) {
        List<FrontLineWorker> frontLineWorkersWithSameMsisdn = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkersWithSameMsisdn.size() != 1 || hasDuplicatesInCSV(frontLineWorkerRequest, frontLineWorkerRequests)) {
            return FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location, shouldSync);
        }
        return FrontLineWorkerMapper.mapFrom(frontLineWorkersWithSameMsisdn.get(0), frontLineWorkerRequest, location, shouldSync);

    }

    private boolean hasDuplicatesInCSV(FrontLineWorkerRequest frontLineWorkerRequest, List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        return CollectionUtils.cardinality(frontLineWorkerRequest, frontLineWorkerRequests) != 1;
    }

    private List<FrontLineWorker> existingFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? Collections.<FrontLineWorker>emptyList() : allFrontLineWorkers.getByMsisdn(FrontLineWorkerMapper.formatMsisdn(msisdn));
    }
}