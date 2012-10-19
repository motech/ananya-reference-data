package org.motechproject.ananya.referencedata.flw.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerCsvRequest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.regex.Pattern;

public class FrontLineWorkerCsvRequestValidator {
    public FLWValidationResponse validate(FrontLineWorkerCsvRequest frontLineWorkerCsvRequest, Location location) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();

        validateMsisdn(flwValidationResponse, frontLineWorkerCsvRequest.getMsisdn());
        validateLocation(location, flwValidationResponse);
        validateName(frontLineWorkerCsvRequest, flwValidationResponse);

        return flwValidationResponse;
    }

    private void validateMsisdn(FLWValidationResponse flwValidationResponse, String msisdn) {
        if (PhoneNumber.isNotValidWithBlanksAllowed(msisdn)) {
            flwValidationResponse.forInvalidMsisdn();
        }
    }

    private void validateLocation(Location location, FLWValidationResponse flwValidationResponse) {
        if (location == null) {
            flwValidationResponse.forInvalidLocation();
        }
    }

    private void validateName(FrontLineWorkerCsvRequest frontLineWorkerWebRequest, FLWValidationResponse flwValidationResponse) {
        String name = frontLineWorkerWebRequest.getName();
        if (StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name)) {
            flwValidationResponse.forInvalidName();
        }
    }
}
