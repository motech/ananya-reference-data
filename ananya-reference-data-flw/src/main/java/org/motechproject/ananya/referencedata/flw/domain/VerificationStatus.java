package org.motechproject.ananya.referencedata.flw.domain;

public enum VerificationStatus {
    SUCCESS,
    INVALID,
    OTHERS;

    public static boolean isValid(String status) {
        try {
            VerificationStatus.valueOf(status);
        } catch (Exception e) {
            return false;
        }
        return true;

    }
}
