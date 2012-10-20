package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

public class FrontLineWorkerWebRequestValidator {

    public static Errors validate(FrontLineWorkerWebRequest frontLineWorkerWebRequest){
        Errors errors = new Errors();
        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getGuid()))
            errors.add("Guid field has invalid/blank value");

        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getVerificationStatus()))
            errors.add("Verification-Status field has invalid/blank value");

        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getReason()))
            errors.add("Reason field has invalid/blank value");
        return errors;
    }
}
