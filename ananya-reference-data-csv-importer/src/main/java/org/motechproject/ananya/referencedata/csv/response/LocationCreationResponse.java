package org.motechproject.ananya.referencedata.csv.response;

import org.apache.commons.lang.StringUtils;

public class LocationCreationResponse {
    private String message;

    public LocationCreationResponse withCreated() {
        this.message = "Location created successfully";
        return this;
    }

    public LocationCreationResponse withValidationResponse(LocationValidationResponse locationValidationResponse) {
        this.message = StringUtils.join(locationValidationResponse.getMessage(), ',');
        return this;
    }

    public String getMessage() {
        return message;
    }
}
