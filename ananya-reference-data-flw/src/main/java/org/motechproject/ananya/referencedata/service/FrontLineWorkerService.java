package org.motechproject.ananya.referencedata.service;

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

import java.util.ArrayList;
import java.util.List;

@Service
public class FrontLineWorkerService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;
    private SyncService syncService;

    @Autowired
    public FrontLineWorkerService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers, SyncService syncService) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.syncService = syncService;
    }

    public FrontLineWorkerResponse add(FrontLineWorkerRequest frontLineWorkerRequest) {
        FrontLineWorkerResponse frontLineWorkerResponse = new FrontLineWorkerResponse();

        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validateCreateRequest(frontLineWorkerRequest, location);
        if (!FLWValidationResponse.isValid())
            return frontLineWorkerResponse.withValidationResponse(FLWValidationResponse);

        if (existingFLW(frontLineWorkerRequest) != null)
            return frontLineWorkerResponse.withFLWExists();

        FrontLineWorker frontLineWorker = FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, location);
        allFrontLineWorkers.add(frontLineWorker);
        syncService.syncFrontLineWorker(frontLineWorker.getId());
        return frontLineWorkerResponse.withCreated();
    }

    public FrontLineWorkerResponse update(FrontLineWorkerRequest frontLineWorkerRequest) {
        FrontLineWorkerResponse frontLineWorkerResponse = new FrontLineWorkerResponse();

        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validateUpdateRequest(frontLineWorkerRequest, location);
        if (!FLWValidationResponse.isValid())
            return frontLineWorkerResponse.withValidationResponse(FLWValidationResponse);

        FrontLineWorker frontLineWorkerInDB = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkerInDB == null)
            return add(frontLineWorkerRequest);

        FrontLineWorker frontLineWorker = FrontLineWorkerMapper.mapFrom(frontLineWorkerInDB, frontLineWorkerRequest, location);
        allFrontLineWorkers.update(frontLineWorker);
        syncService.syncFrontLineWorker(frontLineWorker.getId());
        return frontLineWorkerResponse.withUpdated();
    }

    public void addAllWithoutValidations(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        for(FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            LocationRequest location = frontLineWorkerRequest.getLocation();
            frontLineWorkers.add(FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest, allLocations.getFor(location.getDistrict(), location.getBlock(), location.getPanchayat())));
        }
        allFrontLineWorkers.addAll(frontLineWorkers);
    }

    public FrontLineWorker getById(Integer id) {
        return allFrontLineWorkers.getById(id);
    }

    private FrontLineWorker existingFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? null : allFrontLineWorkers.getByMsisdn(Long.valueOf(FrontLineWorkerMapper.formatMsisdn(msisdn)));
    }
}