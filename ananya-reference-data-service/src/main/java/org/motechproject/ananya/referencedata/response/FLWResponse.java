package org.motechproject.ananya.referencedata.response;

public class FLWResponse {
    private String message = "FLW created successfully";

    public String getMessage() {
        return message;
    }

    public FLWResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = validationResponse.getMessage();
        return this;
    }
}
