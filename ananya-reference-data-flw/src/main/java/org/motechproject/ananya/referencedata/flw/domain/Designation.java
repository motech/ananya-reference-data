package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum Designation {
    ASHA,
    ANM,
    AWW;

    private static Designation from(String string) {
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

    public static Designation getFor(String designation) {
        return isValid(designation) ? from(designation) : null;
    }
}
