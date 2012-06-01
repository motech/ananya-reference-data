package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.ValidationResponse;

public class FLWValidator {
    public ValidationResponse validateCreateRequest(FLWRequest flwRequest, Location location) {
        ValidationResponse validationResponse = new ValidationResponse();

        String msisdn = flwRequest.getMsisdn();
        if(!StringUtils.isBlank(msisdn) && (msisdn.length() < 10 || !StringUtils.isNumeric(msisdn))){
            validationResponse.forInvalidMsisdn();
        }

        String designation = flwRequest.getDesignation();
        if(designation != null && !Designation.contains(designation)){
            validationResponse.forInvalidDesignation();
        }
        
        if(location == null){
            validationResponse.forInvalidLocation();
        }

        return validationResponse;
    }

    public ValidationResponse validateUpdateRequest(FLWRequest flwRequest, Location location) {
        ValidationResponse validationResponse = new ValidationResponse();

        String msisdn = flwRequest.getMsisdn();
        String designation = flwRequest.getDesignation();
        if(msisdn.length() < 10 || !StringUtils.isNumeric(msisdn)){
            validationResponse.forInvalidMsisdn();
        }
        if(designation != null && !Designation.contains(designation)){
            validationResponse.forInvalidDesignation();
        }
        if(location == null){
            validationResponse.forInvalidLocation();
        }

        return validationResponse;
    }
}
