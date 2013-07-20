package org.motechproject.ananya.referencedata.csv.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.isValidWithBlanksAllowed;

public class FrontLineWorkerImportRequestValidator {
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerImportRequestValidator(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public FrontLineWorkerImportValidationResponse validate(FrontLineWorkerImportRequest request, Location location) {
        FrontLineWorkerImportValidationResponse response = new FrontLineWorkerImportValidationResponse();

        validateMsisdn(request, response);
        validateLocation(location, response);
        validateName(request, response);
        validateId(request, response);
        validateAlternateContactNumber(request, response);
        validateVerificationStatus(request, response);

        return response;
    }

    public void validateVerificationStatus(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        String verificationStatus = request.getVerificationStatus();
        if (isBlank(verificationStatus)) {
            validateWithFLWWithNonBlankVerificationStatusFromDB(request, response);

        } else {
            if (!VerificationStatus.isValid(verificationStatus)) {
                response.forInvalidVerificationStatus();
            }
        }
    }

    public void validateAlternateContactNumber(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        if (!isValidWithBlanksAllowed(request.getAlternateContactNumber())) {
            response.forInvalidAlternateContactNumber();
        }
    }

    public void validateId(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        String id = request.getId();
        if (isBlank(id) || StringUtils.equals(FrontLineWorker.DEFAULT_UUID_STRING, id))
            return;
        else {
            if (!Pattern.matches(FrontLineWorker.FLW_ID_FORMAT, id)) {
                response.forInvalidId();
            } else {
                validateFlwIdWithFlwFromDB(request, response);
            }
        }

    }

    public void validateMsisdn(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        if (!isValidWithBlanksAllowed(request.getMsisdn())) {
            response.forInvalidMsisdn();
        }
    }

    public void validateLocation(Location location, FrontLineWorkerImportValidationResponse response) {
        if (location == null) {
            response.forInvalidLocation();
        }
    }

    public void validateName(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        String name = request.getName();
        if (ValidationUtils.isInvalidNameWithBlankAllowed(name)) {
            response.forInvalidName();
        }
    }

    private void validateFlwIdWithFlwFromDB(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        FrontLineWorker byFlwId = allFrontLineWorkers.getByFlwId(UUID.fromString(request.getId()));
        if (byFlwId == null) {
            response.forMissingFLW();
        } else {
            try {
                Long requestMsisdn = formatPhoneNumber(request.getMsisdn());
                if (!requestMsisdn.equals(byFlwId.getMsisdn())) {
                    response.forNonMatchingMsisdn();
                } else if (isNotBlank(byFlwId.getVerificationStatus()) && isBlank(request.getVerificationStatus())) {
                    response.forInvalidBlankVerificationStatus();
                }
            } catch (NumberFormatException e) {
                response.forInvalidMsisdn();
            }

        }
    }

    private void validateWithFLWWithNonBlankVerificationStatusFromDB(FrontLineWorkerImportRequest request, FrontLineWorkerImportValidationResponse response) {
        try {
            int size = allFrontLineWorkers.getByMsisdnWithStatus(formatPhoneNumber(request.getMsisdn())).size();
            if (size != 0)
                response.forInvalidBlankVerificationStatus();
        } catch (NumberFormatException e) {
            response.forInvalidMsisdn();
        }

    }

}
