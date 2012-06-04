package org.motechproject.ananya.referencedata.domain;

public enum Designation {
    ASHA,
    ANM,
    ANGANWADI,
    INVALID;

    public static boolean contains(String designation) {
        Designation[] designationValues = Designation.values();

        for (Designation value : designationValues) {
            if (value.name().equalsIgnoreCase(designation))
                return true;
        }
        return false;
    }
}
