package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;

public enum Designation {
    ASHA,
    ANM,
    AWW;

    public static boolean contains(String designation) {
        for (Designation value : Designation.values()) {
            if (value.name().equalsIgnoreCase(StringUtils.trimToEmpty(designation)))
                return true;
        }
        return false;
    }

    public static Designation getFor(String designation) {
        return Designation.contains(designation) ? Designation.valueOf(designation.trim().toUpperCase()) : null;
    }
}
