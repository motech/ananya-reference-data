package org.motechproject.ananya.referencedata.response;

import org.apache.commons.lang.StringUtils;

public class FLWResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public FLWResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = StringUtils.join(validationResponse.getMessage(), ',');
        return this;
    }

    public FLWResponse withUpdated() {
        this.message = "FLW updated successfully";
        return this;
    }

    public FLWResponse withCreated() {
        this.message = "FLW created successfully";
        return this;
    }

    public FLWResponse withFLWExists() {
        this.message = "FLW already exists";
        return this;
    }
}
