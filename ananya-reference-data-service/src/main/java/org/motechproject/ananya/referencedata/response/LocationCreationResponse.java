package org.motechproject.ananya.referencedata.response;

import org.motechproject.ananya.referencedata.domain.Location;

public class LocationCreationResponse {
    private Location location;
    private String message;

    public LocationCreationResponse(Location location) {
        this.location = location;
    }

    public LocationCreationResponse withValidationResponse(ValidationResponse validationResponse) {
        this.message = validationResponse.getMessage();
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }
}
