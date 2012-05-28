package org.motechproject.ananya.referencedata.response;

import org.motechproject.ananya.referencedata.domain.Location;

public class LocationCreationResponse {
    private ValidationResponse validationResponse;
    private Location location;

    public LocationCreationResponse(Location location) {
        this.location = location;
    }

    public LocationCreationResponse withValidationResponse(ValidationResponse validationResponse) {
        this.validationResponse = validationResponse;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "message = \"" + validationResponse + "\"" +
                ", location =" + location +
                '}';
    }
}
