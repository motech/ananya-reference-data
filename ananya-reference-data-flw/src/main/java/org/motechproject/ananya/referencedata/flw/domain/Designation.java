package org.motechproject.ananya.referencedata.flw.domain;

public enum Designation {
    ASHA,
    ANM,
    AWW,
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
