package org.motechproject.ananya.referencedata.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.mapper.FLWMapper;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;
import org.motechproject.ananya.referencedata.response.ValidationResponse;
import org.motechproject.ananya.referencedata.validators.FLWValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FLWService {

    private AllLocations allLocations;
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FLWService(AllLocations allLocations, AllFrontLineWorkers allFrontLineWorkers) {
        this.allLocations = allLocations;
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public FLWResponse add(FLWRequest flwRequest) {
        Location location = allLocations.getFor(flwRequest.getDistrict(), flwRequest.getBlock(), flwRequest.getPanchayat());
        ValidationResponse validationResponse = new FLWValidator().validate(flwRequest, location);
        if(!validationResponse.isValid())
            return new FLWResponse().withValidationResponse(validationResponse);

        allFrontLineWorkers.add(FLWMapper.mapFrom(flwRequest, location));
        return new FLWResponse().withCreated();
    }

    public FLWResponse update(FLWRequest flwRequest) {
        Location location = allLocations.getFor(flwRequest.getDistrict(), flwRequest.getBlock(), flwRequest.getPanchayat());
        ValidationResponse validationResponse = new FLWValidator().validate(flwRequest, location);
        if(!validationResponse.isValid())
            return new FLWResponse().withValidationResponse(validationResponse);

        String msisdn = flwRequest.getMsisdn();
        FrontLineWorker existingFrontLineWorker = StringUtils.isBlank(msisdn) ? null : allFrontLineWorkers.getFor(Long.valueOf(msisdn));
        if(existingFrontLineWorker == null) {
            return add(flwRequest);
        }

        allFrontLineWorkers.update(FLWMapper.mapFrom(existingFrontLineWorker, flwRequest, location));
        return new FLWResponse().withUpdated();
    }
}