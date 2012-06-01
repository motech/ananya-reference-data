package org.motechproject.ananya.referencedata.response;

import org.apache.commons.lang.StringUtils;

public class LocationCreationResponse {
    private String message;

    public LocationCreationResponse withCreated() {
        this.message = "Location created successfully";
        return this;
    }

    public LocationCreationResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = StringUtils.join(validationResponse.getMessage(), ',');
        return this;
    }

    public String getMessage() {
        return message;
    }
}
