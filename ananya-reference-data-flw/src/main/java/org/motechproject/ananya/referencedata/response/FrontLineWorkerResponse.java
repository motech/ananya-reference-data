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

    public FrontLineWorkerResponse withUpdated() {
        this.message = "FLW updated successfully";
        return this;
    }

    public FrontLineWorkerResponse withCreated() {
        this.message = "FLW created successfully";
        return this;
    }

    public FrontLineWorkerResponse withFLWExists() {
        this.message = "FLW already exists with the same MSISDN number";
        return this;
    }
}
