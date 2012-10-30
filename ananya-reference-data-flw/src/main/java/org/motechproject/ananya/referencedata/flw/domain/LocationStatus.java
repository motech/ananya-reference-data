package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum LocationStatus {
    VALID,
    INVALID,
    NOT_VERIFIED,
    IN_REVIEW;

    public static LocationStatus getFor(String status) {
        for(LocationStatus locationStatus : values()) {
            if(locationStatus.name().equalsIgnoreCase(status.trim()))
                return locationStatus;
        }
        return null;
    }

    public static LocationStatus from(String string) {
        return LocationStatus.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String status) {
        try {
            from(status);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static boolean isInvalidStatus(String status) {
        return isValid(status) && INVALID.equals(from(status));
    }
}
