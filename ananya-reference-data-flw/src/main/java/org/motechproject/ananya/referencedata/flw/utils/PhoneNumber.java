package org.motechproject.ananya.referencedata.flw.utils;


import org.apache.commons.lang.StringUtils;

public class PhoneNumber {
    private Long phoneNumber;

    public PhoneNumber(String phoneNumber) {
        this(phoneNumber, true);
    }

    public PhoneNumber(String phoneNumber, boolean allowPrefix) {
        if (!isValid(phoneNumber, false, allowPrefix)) this.phoneNumber = null;
        this.phoneNumber = formatPhoneNumber(phoneNumber);
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public static boolean isValid(String phoneNumber) {
        return validate(phoneNumber, false, true);
    }

    public static boolean isValidWithBlanksAllowed(String phoneNumber) {
        return validate(phoneNumber, true, true);
    }

    public static boolean isValid(String phoneNumber, boolean allowBlanks, boolean allowPrefix) {
        return validate(phoneNumber, allowBlanks, allowPrefix);
    }

    public static Long formatPhoneNumber(String phoneNumber) {
        Long returnValue = null;
        if (phoneNumber.length() == 10)
            returnValue = Long.parseLong("91" + phoneNumber);
        if (phoneNumber.length() == 12 && (phoneNumber.startsWith("91") || phoneNumber.startsWith("00")))
            returnValue = Long.parseLong("91" + phoneNumber.substring(2, 12));
        return returnValue;
    }

    private static boolean validate(String phoneNumber, boolean allowBlanks, boolean allowPrefix) {
        if (allowBlanks && StringUtils.isBlank(phoneNumber)) return true;
        return StringUtils.isNotBlank(phoneNumber) && StringUtils.isNumeric(phoneNumber) &&
                (phoneNumber.length() == 10 || (allowPrefix && phoneNumber.length() == 12 && (phoneNumber.startsWith("91") || phoneNumber.startsWith("00"))));
    }

}
