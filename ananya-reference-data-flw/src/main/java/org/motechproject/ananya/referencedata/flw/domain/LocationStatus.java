package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum LocationStatus {
    VALID,
    INVALID,
    NOT_VERIFIED,
    IN_REVIEW,
    NEW;

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

    public static boolean isUpdatable(String status) {
        return isNotVerifiedStatus(status) || isInReviewStatus(status);
    }

    public static boolean isValidCsvStatus(String status) {
        return contains(status) && !isNotVerifiedStatus(status);
    }
}