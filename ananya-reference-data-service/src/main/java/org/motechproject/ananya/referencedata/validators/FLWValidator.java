package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.ValidationResponse;

public class FLWValidator {
    public ValidationResponse validate(FLWRequest flwRequest, Location location) {
        ValidationResponse validationResponse = new ValidationResponse();

        String msisdn = flwRequest.getMsisdn();
        String designation = flwRequest.getDesignation();
        if(StringUtils.length(msisdn) < 10 || !StringUtils.isNumeric(msisdn)){
            return validationResponse.forInvalidMsisdn();
        }
        if(designation != null && !Designation.contains(designation)){
            return validationResponse.forInvalidDesignation();
        }
        if(location == null){
            return validationResponse.forInvalidLocation();
        }

        return validationResponse;
    }
}
