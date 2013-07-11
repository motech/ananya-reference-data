package org.motechproject.ananya.referencedata.csv.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.utils.ValidationUtils;

import java.util.regex.Pattern;

public class FrontLineWorkerImportRequestValidator {
    public FrontLineWorkerImportValidationResponse validate(FrontLineWorkerImportRequest frontLineWorkerImportRequest, Location location) {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = new FrontLineWorkerImportValidationResponse();

        validateMsisdn(frontLineWorkerImportValidationResponse, frontLineWorkerImportRequest.getMsisdn());
        validateLocation(location, frontLineWorkerImportValidationResponse);
        validateName(frontLineWorkerImportRequest, frontLineWorkerImportValidationResponse);
        validateId(frontLineWorkerImportRequest, frontLineWorkerImportValidationResponse);
        validateAlternateContactNumber(frontLineWorkerImportRequest, frontLineWorkerImportValidationResponse);
        validateVerificationStatus(frontLineWorkerImportRequest, frontLineWorkerImportValidationResponse);

        return frontLineWorkerImportValidationResponse;
    }

    private void validateVerificationStatus(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        String verificationStatus = request.getVerificationStatus();
        if (StringUtils.isNotBlank(verificationStatus) && !VerificationStatus.isValid(verificationStatus)){
            response.forInvalidVerificationStatus();
        }
    }

    private void validateAlternateContactNumber(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        if (!PhoneNumber.isValidWithBlanksAllowed(request.getAlternateContactNumber())) {
            response.forInvalidAlternateContactNumber();
        }
    }

    private void validateId(FrontLineWorkerImportRequest frontLineWorkerImportRequest, FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse) {
        String id = frontLineWorkerImportRequest.getId();
        if (StringUtils.isNotBlank(id) && !Pattern.matches(FrontLineWorker.FLW_ID_FORMAT, id)) {
            frontLineWorkerImportValidationResponse.forInvalidId();
        }
    }

    private void validateMsisdn(FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse, String msisdn) {
        if (!PhoneNumber.isValidWithBlanksAllowed(msisdn)) {
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
