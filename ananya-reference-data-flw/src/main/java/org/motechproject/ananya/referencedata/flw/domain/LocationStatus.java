package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum LocationStatus {
    NEW{
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            return false;
        }
    },
    VALID{
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>(){{
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    },
    INVALID{
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>(){{
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    },
    NOT_VERIFIED{
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>(){{
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
                add(LocationStatus.IN_REVIEW);
            }};
            return validStates.contains(toStatus);
        }
    },
    IN_REVIEW{
        @Override
        public boolean canTransitionTo(LocationStatus toStatus) {
            List<LocationStatus> validStates = new ArrayList<LocationStatus>(){{
                add(LocationStatus.IN_REVIEW);
                add(LocationStatus.VALID);
                add(LocationStatus.INVALID);
            }};
            return validStates.contains(toStatus);
        }
    };

    public abstract boolean canTransitionTo(LocationStatus toStatus);

    public static LocationStatus from(String string) {
        try {
            return LocationStatus.valueOf(StringUtils.trimToEmpty(StringUtils.upperCase(string)));
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean contains(String status) {
        return from(status) != null;
    }

    public static boolean isValidStatus(String status) {
        return VALID.equals(from(status));
    }

    public static boolean isInvalidStatus(String status) {
        return INVALID.equals(from(status));
    }

    public static boolean isNewStatus(String status) {
        return NEW.equals(from(status));
    }

    public static boolean isNotVerifiedStatus(String status) {
        return NOT_VERIFIED.equals(from(status));
    }

    private static boolean isInReviewStatus(String status) {
        return IN_REVIEW.equals(from(status));
    }

    public static boolean isValidAlternateLocationStatus(String status) {
        return isValidStatus(status) || isNewStatus(status);
    }

    public static boolean isValidCsvStatus(String status) {
        return contains(status) && !isNotVerifiedStatus(status);
    }

    public static boolean isValidOrInReviewStatus(String status) {
        return isValidStatus(status) || isInReviewStatus(status);
    }
}