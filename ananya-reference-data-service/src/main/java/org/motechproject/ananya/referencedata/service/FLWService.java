package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.mapper.FLWMapper;
import org.motechproject.ananya.referencedata.repository.AllFLWData;
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
    private AllFLWData allFlwData;

    @Autowired
    public FLWService(AllLocations allLocations, AllFLWData allFlwData) {
        this.allLocations = allLocations;
        this.allFlwData = allFlwData;
    }

    public FLWResponse add(FLWRequest flwRequest) {
        Location location = allLocations.getFor(flwRequest.getDistrict(), flwRequest.getBlock(), flwRequest.getPanchayat());
        ValidationResponse validationResponse = new FLWValidator().validate(flwRequest, location);

        if(!validationResponse.isValid())
            return new FLWResponse().withValidationResponse(validationResponse);

        allFlwData.add(FLWMapper.mapFrom(flwRequest, location));
        return new FLWResponse();
    }
}