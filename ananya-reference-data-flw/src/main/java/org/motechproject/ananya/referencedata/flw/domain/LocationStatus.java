package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum LocationStatus {
    NEW("NEW") {
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            return false;
        }
    },
    VALID("VALID") {
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    },
    INVALID("INVALID") {
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    },
    NOT_VERIFIED("NOT VERIFIED") {
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
                add(LocationStatus.IN_REVIEW);
            }};
            return validStates.contains(toStatus);
        }
    },
    IN_REVIEW("IN REVIEW") {
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>() {{
                add(LocationStatus.IN_REVIEW);
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    };

    private String description;

    LocationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public abstract boolean canTransitionTo(LocationStatus toStatus);

    public static LocationStatus from(String status) {
        if (status != null) {
            for (LocationStatus locationStatus : LocationStatus.values()) {
                if (StringUtils.trimToEmpty(status).equalsIgnoreCase(locationStatus.description)) {
                    return locationStatus;
                }
            }
        }
        return null;
    }

    public static boolean isValid(String status) {
        return from(status) != null;
    }

    public boolean isValidStatusForAlternateLocation() {
        return this == VALID || this == NEW;
    }

    public boolean isValidCsvStatus() {
        return this != NOT_VERIFIED;
    }

    public boolean isValidOrInReviewStatus() {
        return this == VALID || this == IN_REVIEW;
    }
}