package org.motechproject.ananya.referencedata.web.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.request.FrontLineWorkerWebRequest;

public class FrontLineWorkerWebRequestValidator {

    private Errors errors = new Errors();

    public Errors validateFrontLineWorkerRequest(FrontLineWorkerWebRequest frontLineWorkerWebRequest){
        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getReason()))
            errors.add("Reason field has invalid/blank value");

        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getGuid()))
            errors.add("Guid field has invalid/blank value");

        if(StringUtils.isEmpty(frontLineWorkerWebRequest.getVerificationStatus()))
            errors.add("Verification-Status field has invalid/blank value");

        return errors;
    }
}
