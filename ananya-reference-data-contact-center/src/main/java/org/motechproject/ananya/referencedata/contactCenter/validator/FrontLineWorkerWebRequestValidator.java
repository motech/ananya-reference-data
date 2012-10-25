package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

public class FrontLineWorkerWebRequestValidator {

    public static Errors validate(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        Errors errors = new Errors();
        validateFlwId(frontLineWorkerWebRequest.getFlwId(), errors);
        validateMsisdn(frontLineWorkerWebRequest.getMsisdn(), errors);
        String verificationStatus = frontLineWorkerWebRequest.getVerificationStatus();
        validateVerificationStatus(verificationStatus, errors);
        if (errors.hasNoErrors()) {
            if (VerificationStatus.isSuccess(verificationStatus))
                validateSuccessfulRegistrationRequest(frontLineWorkerWebRequest, errors);
            else
                validateUnsuccessfulRegistrationRequest(frontLineWorkerWebRequest, errors);
        }
        return errors;
    }

    private static void validateMsisdn(String msisdn, Errors errors) {
        if (PhoneNumber.isNotValid(msisdn)) {
            errors.add("msisdn field has invalid/blank value");
        }
    }

    private static void validateFlwId(String flwId, Errors errors) {
        if (StringUtils.isEmpty(flwId))
            errors.add("id field is blank");
    }

    private static void validateVerificationStatus(String verificationStatus, Errors errors) {
        if (StringUtils.isEmpty(verificationStatus) || !VerificationStatus.isValid(verificationStatus))
            errors.add("verificationStatus field has invalid/blank value");
    }

    private static void validateUnsuccessfulRegistrationRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest, Errors errors) {
        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getReason()))
            errors.add("reason field is blank");
    }

    private static void validateSuccessfulRegistrationRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest, Errors errors) {
        String designation = frontLineWorkerWebRequest.getDesignation();
        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getName()))
            errors.add("name field is blank");
        if (StringUtils.isEmpty(designation) || !Designation.isValid(designation))
            errors.add("designation field has invalid/blank value");
        if (StringUtils.isNotEmpty(frontLineWorkerWebRequest.getReason()))
            errors.add("Reason field should not be a part of the request");

        errors.addAll(LocationWebRequestValidator.validate(frontLineWorkerWebRequest.getLocation()));
    }
}