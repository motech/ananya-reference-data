package org.motechproject.ananya.referencedata.web.validator;

import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;

public class FrontLineWorkerWebRequestValidator {

    private Errors errors = new Errors();

    public Errors validateFrontLineWorkerRequest(FrontLineWorkerRequest frontLineWorkerRequest){
        String reason = frontLineWorkerRequest.getReason();
        if(isNullOrEmpty(reason))
            errors.add("Reason field has invalid/blank value");

        if(isNullOrEmpty(frontLineWorkerRequest.getGuid()))
            errors.add("Guid field has invalid/blank value");

        if(isNullOrEmpty(frontLineWorkerRequest.getVerificationStatus()))
            errors.add("Verification-Status field has invalid/blank value");

        return errors;
    }

    private boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }


}
