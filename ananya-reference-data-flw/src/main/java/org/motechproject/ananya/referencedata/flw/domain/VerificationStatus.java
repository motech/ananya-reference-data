package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum VerificationStatus {
    SUCCESS,
    INVALID,
    OTHER;

    public static VerificationStatus from(String string) {
        if(StringUtils.isBlank(string))return null;
        return VerificationStatus.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String status) {
        try {
            return from(status) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
