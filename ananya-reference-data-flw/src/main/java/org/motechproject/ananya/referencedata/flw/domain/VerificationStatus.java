package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum VerificationStatus {
    SUCCESS,
    INVALID,
    OTHERS;

    public static VerificationStatus from(String string) {
        return VerificationStatus.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String status) {
        try {
            from(status);
        } catch (Exception e) {
            return false;
        }
        return true;

    }
}
