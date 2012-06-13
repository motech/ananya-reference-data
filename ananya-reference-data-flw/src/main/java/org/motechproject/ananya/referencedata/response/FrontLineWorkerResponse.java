package org.motechproject.ananya.referencedata.response;

import org.apache.commons.lang.StringUtils;

public class FrontLineWorkerResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public FrontLineWorkerResponse withValidationResponse(FLWValidationResponse FLWValidationResponse) {
        this.message = StringUtils.join(FLWValidationResponse.getMessage(), ',');
        return this;
    }

    public FrontLineWorkerResponse withCreatedOrUpdated() {
        this.message = "FLW created/updated successfully";
        return this;
    }
}
