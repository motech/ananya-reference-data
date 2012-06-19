package org.motechproject.ananya.referencedata.flw.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.common.domain.PhoneNumber;

import java.util.regex.Pattern;

public class FrontLineWorkerValidator {
    public FLWValidationResponse validate(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();

        validateMsisdn(flwValidationResponse, frontLineWorkerRequest.getMsisdn());
        validateLocation(location, flwValidationResponse);
        validateName(frontLineWorkerRequest, flwValidationResponse);

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
