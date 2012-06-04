package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.ValidationResponse;

public class FLWValidator {
    public ValidationResponse validateCreateRequest(FLWRequest flwRequest, Location location) {
        ValidationResponse validationResponse = new ValidationResponse();

        String msisdn = flwRequest.getMsisdn();
        if (!StringUtils.isBlank(msisdn) && anInvalidMsisdn(msisdn)) { // Blank msisdn is considered Valid for create request
            validationResponse.forInvalidMsisdn();
        }

        validateDesignationAndLocation(flwRequest, location, validationResponse);

        return validationResponse;
    }

    public ValidationResponse validateUpdateRequest(FLWRequest flwRequest, Location location) {
        ValidationResponse validationResponse = new ValidationResponse();

        if (anInvalidMsisdn(flwRequest.getMsisdn())) {
            validationResponse.forInvalidMsisdn();
        }

        validateDesignationAndLocation(flwRequest, location, validationResponse);

        return validationResponse;
    }

    private boolean anInvalidMsisdn(String msisdn) {
        return StringUtils.length(msisdn) < 10 || !StringUtils.isNumeric(msisdn);
    }

    private void validateDesignationAndLocation(FLWRequest flwRequest, Location location, ValidationResponse validationResponse) {
        String designation = flwRequest.getDesignation();
        if (designation != null && !Designation.contains(designation)) {
            validationResponse.forInvalidDesignation();
        }

        if (location == null) {
            validationResponse.forInvalidLocation();
        }
    }
}
