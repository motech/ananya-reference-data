package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;

import java.util.regex.Pattern;

public class FrontLineWorkerValidator {
    public FLWValidationResponse validate(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();

        String msisdn = frontLineWorkerRequest.getMsisdn();
        if (!StringUtils.isBlank(msisdn) && anInvalidMsisdn(msisdn)) {
            flwValidationResponse.forInvalidMsisdn();
        }

        validateLocation(location, flwValidationResponse);
        validateName(frontLineWorkerRequest, flwValidationResponse);

        return flwValidationResponse;
    }

    private void validateLocation(Location location, FLWValidationResponse flwValidationResponse) {
        if (location == null) {
            flwValidationResponse.forInvalidLocation();
        }
    }

    private void validateName(FrontLineWorkerRequest frontLineWorkerRequest, FLWValidationResponse flwValidationResponse) {
        String name = frontLineWorkerRequest.getName();
        if (StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name)) {
            flwValidationResponse.forInvalidName();
        }
    }

    private boolean anInvalidMsisdn(String msisdn) {
        return StringUtils.length(msisdn) < 10 ||
                !StringUtils.isNumeric(msisdn) ||
                (StringUtils.length(msisdn) == 12 && !(StringUtils.startsWith(msisdn, "91") || StringUtils.startsWith(msisdn, "00"))) ||
                (msisdn.length() > 10 && msisdn.length() != 12);
    }
}
