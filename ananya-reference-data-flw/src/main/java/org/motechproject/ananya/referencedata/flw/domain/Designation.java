package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum Designation {
    ASHA,
    ANM,
    AWW;

    public static Designation from(String string) {
        return Designation.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String designation) {
        try {
            from(designation);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
