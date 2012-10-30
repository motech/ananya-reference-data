package org.motechproject.ananya.referencedata.flw.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isInvalidNameWithBlankAllowed(String name){
        return StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }

    public static boolean isInvalidName(String name) {
        return StringUtils.isBlank(name) || !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }
}
