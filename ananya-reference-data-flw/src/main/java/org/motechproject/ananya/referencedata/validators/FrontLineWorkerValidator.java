package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;

import java.util.List;

public class FrontLineWorkerValidator {
    public FLWValidationResponse validateCreateRequest(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        FLWValidationResponse FLWValidationResponse = new FLWValidationResponse();

        String msisdn = frontLineWorkerRequest.getMsisdn();
        if (!StringUtils.isBlank(msisdn) && anInvalidMsisdn(msisdn)) { // Blank msisdn is considered Valid for create request
            FLWValidationResponse.forInvalidMsisdn();
        }

        validateLocation(location, FLWValidationResponse);
        validateName(frontLineWorkerRequest, FLWValidationResponse);

        return FLWValidationResponse;
    }

    public FLWValidationResponse validateUpdateRequest(FrontLineWorkerRequest frontLineWorkerRequest, Location location) {
        FLWValidationResponse FLWValidationResponse = new FLWValidationResponse();

        if (anInvalidMsisdn(frontLineWorkerRequest.getMsisdn())) {
            FLWValidationResponse.forInvalidMsisdn();
        }

        validateLocation(location, FLWValidationResponse);
        validateName(frontLineWorkerRequest, FLWValidationResponse);

        return FLWValidationResponse;
    }

    public FLWValidationResponse validateWithBulkValidation(FrontLineWorkerRequest frontLineWorkerRequest,
                                                            Location location,
                                                            List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        FLWValidationResponse FLWValidationResponse = validateCreateRequest(frontLineWorkerRequest, location);
        if(hasDuplicates(frontLineWorkerRequest, frontLineWorkerRequests))
            FLWValidationResponse.forDuplicates();
        return FLWValidationResponse;
    }

    private boolean hasDuplicates(FrontLineWorkerRequest frontLineWorkerRequest, List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        int count = 0;
        for (FrontLineWorkerRequest flwRequest : frontLineWorkerRequests) {
            if (StringUtils.equals(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()), StringUtils.trimToEmpty(flwRequest.getMsisdn())))
                count++;
            if(count == 2)
                return true;
        }
        return false;
    }

    private void validateLocation(Location location, FLWValidationResponse FLWValidationResponse) {
        if (location == null) {
            FLWValidationResponse.forInvalidLocation();
        }
    }

    private void validateName(FrontLineWorkerRequest frontLineWorkerRequest, FLWValidationResponse FLWValidationResponse) {
        String name = frontLineWorkerRequest.getName();
        if( StringUtils.isNotBlank(name) && !StringUtils.isAlphanumericSpace(name)) {
            FLWValidationResponse.forInvalidName();
        }
    }

    private boolean anInvalidMsisdn(String msisdn) {
        return StringUtils.length(msisdn) < 10 || !StringUtils.isNumeric(msisdn);
    }

}
