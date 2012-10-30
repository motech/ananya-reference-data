package org.motechproject.ananya.referencedata.flw.service.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;

public class LocationSyncRequest {
    private LocationRequest actualLocation;
    private LocationRequest newLocation;
    private String locationStatus;
    private DateTime lastModifiedTime;

    public LocationSyncRequest(LocationRequest actualLocation, LocationRequest newLocation, String locationStatus, DateTime lastModifiedTime) {
        this.actualLocation = actualLocation;
        this.newLocation = newLocation;
        this.locationStatus = locationStatus;
        this.lastModifiedTime = lastModifiedTime;
    }

    public LocationRequest getActualLocation() {
        return actualLocation;
    }

    public LocationRequest getNewLocation() {
        return newLocation;
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    public DateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
}
