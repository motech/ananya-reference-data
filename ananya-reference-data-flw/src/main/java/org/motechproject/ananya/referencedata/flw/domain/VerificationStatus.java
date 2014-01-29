package org.motechproject.ananya.referencedata.flw.domain;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

public enum VerificationStatus {
    SUCCESS,
    INVALID,
    OTHER;

    public static VerificationStatus from(String status) {
        if (isBlank(status)) return null;
        return VerificationStatus.valueOf(trimToEmpty(status).toUpperCase());
    }

    public static boolean isValid(String status) {
        try {
            from(status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isInvalid(String verificationStatus) {
        VerificationStatus givenStatus = from(verificationStatus);
        return VerificationStatus.INVALID.equals(givenStatus);
    }

    public static boolean isOther(String verificationStatus) {
        VerificationStatus givenStatus = from(verificationStatus);
        return VerificationStatus.OTHER.equals(givenStatus);
    }
}
