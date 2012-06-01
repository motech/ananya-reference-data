package org.motechproject.ananya.referencedata.response;

public class FLWResponse {
    private String message;

    public String getMessage() {
        return message;
    }

    public FLWResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = validationResponse.getMessage();
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
