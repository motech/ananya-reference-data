package org.motechproject.ananya.referencedata.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.mapper.FLWMapper;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.validators.FrontLineWorkerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FrontLineWorkerService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public FLWResponse add(FrontLineWorkerRequest frontLineWorkerRequest) {
        FLWResponse flwResponse = new FLWResponse();

        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validateCreateRequest(frontLineWorkerRequest, location);
        if (!FLWValidationResponse.isValid())
            return flwResponse.withValidationResponse(FLWValidationResponse);

        if (existingFLW(frontLineWorkerRequest) != null)
            return flwResponse.withFLWExists();

        allFrontLineWorkers.add(FLWMapper.mapFrom(frontLineWorkerRequest, location));
        return flwResponse.withCreated();
    }

    public FLWResponse update(FrontLineWorkerRequest frontLineWorkerRequest) {
        FLWResponse flwResponse = new FLWResponse();

        LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
        Location location = allLocations.getFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
        FLWValidationResponse FLWValidationResponse = new FrontLineWorkerValidator().validateUpdateRequest(frontLineWorkerRequest, location);
        if (!FLWValidationResponse.isValid())
            return flwResponse.withValidationResponse(FLWValidationResponse);

        FrontLineWorker frontLineWorkerInDB = existingFLW(frontLineWorkerRequest);
        if (frontLineWorkerInDB == null)
            return add(frontLineWorkerRequest);

        allFrontLineWorkers.update(FLWMapper.mapFrom(frontLineWorkerInDB, frontLineWorkerRequest, location));
        return flwResponse.withUpdated();
    }

    public void addAllWithoutValidations(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        for(FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            LocationRequest location = frontLineWorkerRequest.getLocation();
            frontLineWorkers.add(FLWMapper.mapFrom(frontLineWorkerRequest, allLocations.getFor(location.getDistrict(), location.getBlock(), location.getPanchayat())));
        }
        allFrontLineWorkers.addAll(frontLineWorkers);
    }

    private FrontLineWorker existingFLW(FrontLineWorkerRequest frontLineWorkerRequest) {
        String msisdn = frontLineWorkerRequest.getMsisdn();
        return StringUtils.isBlank(msisdn) ? null : allFrontLineWorkers.getFor(Long.valueOf(FLWMapper.formatMsisdn(msisdn)));
    }
}