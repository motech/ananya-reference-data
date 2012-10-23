package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

public class FrontLineWorkerWebRequestValidator {

    public static Errors validate(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        if (VerificationStatus.isSuccess(frontLineWorkerWebRequest.getVerificationStatus()))
            return validateSuccessfulRegistrationRequest(frontLineWorkerWebRequest);
        return validateUnsuccessfulRegistrationRequest(frontLineWorkerWebRequest);

    }

    private static Errors validateUnsuccessfulRegistrationRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        Errors errors = new Errors();
        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getFlwId()))
            errors.add("id field is blank");

        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getVerificationStatus()) || !VerificationStatus.isValid(frontLineWorkerWebRequest.getVerificationStatus()))
            errors.add("verificationStatus field has invalid/blank value");

        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getReason()))
            errors.add("reason field is blank");
        return errors;
    }

    private static Errors validateSuccessfulRegistrationRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest) {
        Errors errors = new Errors();
        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getFlwId()))
            errors.add("id field is blank");

        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getVerificationStatus()) || !VerificationStatus.isValid(frontLineWorkerWebRequest.getVerificationStatus()))
            errors.add("verificationStatus field has invalid/blank value");

        if (StringUtils.isEmpty(frontLineWorkerWebRequest.getName()))
            errors.add("name field is blank");

        String designation = frontLineWorkerWebRequest.getDesignation();
        if (StringUtils.isEmpty(designation) || !Designation.isValid(designation))
            errors.add("designation field has invalid/blank value");

        errors.addAll(LocationWebRequestValidator.validate(frontLineWorkerWebRequest.getLocation()));

        return errors;
    }
}
