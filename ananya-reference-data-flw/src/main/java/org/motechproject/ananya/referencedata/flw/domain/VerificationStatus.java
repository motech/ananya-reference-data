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

    public static boolean isInvalidOrOther(String verificationStatus) {
        VerificationStatus givenStatus = from(verificationStatus);
        return VerificationStatus.INVALID.equals(givenStatus) || VerificationStatus.OTHER.equals(givenStatus);
    }
}
