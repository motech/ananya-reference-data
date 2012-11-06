package org.motechproject.ananya.referencedata.flw.service.request;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import java.io.Serializable;

public class LocationSyncRequest implements Serializable {
    private LocationRequest actualLocation;
    private LocationRequest newLocation;
    private LocationStatus locationStatus;
    private DateTime lastModifiedTime;

    public LocationSyncRequest(LocationRequest actualLocation, LocationRequest newLocation, LocationStatus locationStatus, DateTime lastModifiedTime) {
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

    public LocationStatus getLocationStatus() {
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