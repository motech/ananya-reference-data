package org.motechproject.ananya.referencedata.csv.validator;

import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.utils.ValidationUtils;

public class FrontLineWorkerImportRequestValidator {
    public FrontLineWorkerImportValidationResponse validate(FrontLineWorkerImportRequest frontLineWorkerImportRequest, Location location) {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = new FrontLineWorkerImportValidationResponse();

        validateMsisdn(frontLineWorkerImportValidationResponse, frontLineWorkerImportRequest.getMsisdn());
        validateLocation(location, frontLineWorkerImportValidationResponse);
        validateName(frontLineWorkerImportRequest, frontLineWorkerImportValidationResponse);

        return frontLineWorkerImportValidationResponse;
    }

    private void validateMsisdn(FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse, String msisdn) {
        if (PhoneNumber.isNotValidWithBlanksAllowed(msisdn)) {
            frontLineWorkerImportValidationResponse.forInvalidMsisdn();
        }
    }

    private void validateLocation(Location location, FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse) {
        if (location == null) {
            frontLineWorkerImportValidationResponse.forInvalidLocation();
        }
    }

    private void validateName(FrontLineWorkerImportRequest frontLineWorkerWebRequest, FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse) {
        String name = frontLineWorkerWebRequest.getName();
        if (ValidationUtils.isInvalidNameWithBlankAllowed(name)) {
            frontLineWorkerImportValidationResponse.forInvalidName();
        }
    }
}
