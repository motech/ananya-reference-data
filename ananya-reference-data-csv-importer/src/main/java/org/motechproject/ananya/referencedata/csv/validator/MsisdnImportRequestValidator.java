package org.motechproject.ananya.referencedata.csv.validator;

import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.csv.response.MsisdnImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.utils.FLWValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.ananya.referencedata.flw.utils.FLWValidationUtils.getDuplicateRecordsByField;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.isValid;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.isValidWithBlanksAllowed;

@Component
public class MsisdnImportRequestValidator {

    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public MsisdnImportRequestValidator(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public MsisdnImportValidationResponse validate(List<MsisdnImportRequest> requests, MsisdnImportRequest request) {
        MsisdnImportValidationResponse response = new MsisdnImportValidationResponse();
        validateDuplicates(requests, request, response);
        validateIntegrity(request, response);
        validateMsisdn(request, response);
        validateNewMsisdn(request, response);
        validateAlternateContactNumber(request, response);
        return response;
    }

    private void validateDuplicates(List<MsisdnImportRequest> requests, MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        Collection flwsByMsisdn = getDuplicateRecordsByField(requests, "msisdn", request.getMsisdn());
        if (flwsByMsisdn.size() > 1) {
            response.forDuplicateMsisdnRecords();
        }

        Collection flwsByNewMsisdn = getDuplicateRecordsByField(requests, "newMsisdn", request.getNewMsisdn());
        if (flwsByNewMsisdn.size() > 1) {
            response.forDuplicateNewMsisdnRecords();
        }
    }

    private void validateIntegrity(MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        if (!request.isChangeMsisdn() && !request.isUpdateAlternateContactNumber()) {
            response.forRequestIntegrity();
        }
    }

    private void validateMsisdn(MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        String msisdn = request.getMsisdn();
        if (isBlank(msisdn)) {
            response.forMissingMsisdn();
            return;
        }

        if (!isValid(msisdn)) {
            response.forInvalidMsisdn();
            return;
        }

        validateExistingFlw(request.msisdnAsLong(), response);
    }

    private void validateExistingFlw(Long msisdn, MsisdnImportValidationResponse response) {
        List<FrontLineWorker> frontLineWorkers = allFrontLineWorkers.getByMsisdn(msisdn);

        if (isEmpty(frontLineWorkers)) {
            response.forMissingFlw();
            return;
        }

        if (frontLineWorkers.size() > 1) {
            response.forDuplicateFlwsByMsisdn();
            return;
        }

        validateVerificationStatus(frontLineWorkers.get(0), response);
    }

    private void validateVerificationStatus(FrontLineWorker frontLineWorker, MsisdnImportValidationResponse response) {
        String verificationStatus = frontLineWorker.getVerificationStatus();
        if (VerificationStatus.isInvalid(verificationStatus)) {
            response.forInvalidVerificationStatus();
            return;
        }

        if (VerificationStatus.isOther(verificationStatus)) {
            response.forOtherVerificationStatus();
        }
    }

    private void validateNewMsisdn(MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        String newMsisdn = request.getNewMsisdn();
        if (!isValidWithBlanksAllowed(newMsisdn)) {
            response.forInvalidNewMsisdn();
            return;
        }

        validateFlwByNewMsisdn(request, response);
    }

    private void validateFlwByNewMsisdn(MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        if (!request.isChangeMsisdn())
            return;

        List<FrontLineWorker> flwsByNewMsisdn = allFrontLineWorkers.getByMsisdn(request.newMsisdnAsLong());
        if (flwsByNewMsisdn != null && flwsByNewMsisdn.size() > 1) {
            response.forDuplicateFlwsByNewMsisdn();
        }
    }

    private void validateAlternateContactNumber(MsisdnImportRequest request, MsisdnImportValidationResponse response) {
        if (!FLWValidationUtils.isValidAlternateContactNumber(request.getAlternateContactNumber())) {
            response.forInvalidAlternateContactNumber();
        }
    }
}
