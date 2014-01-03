package org.motechproject.ananya.referencedata.flw.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.collections.CollectionUtils.select;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.isValidWithBlanksAllowed;

public class FLWValidationUtils {
    public static boolean isInvalidNameWithBlankAllowed(String name) {
        return StringUtils.isNotBlank(name) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }

    public static boolean isInvalidName(String name) {
        return StringUtils.isBlank(name) || !Pattern.matches("[a-zA-Z0-9\\s\\.]*", name);
    }

    public static <T> Collection getDuplicateRecordsByMsisdn(List<T> list, final String msisdn) {
        return select(list, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return msisdn != null && msisdn.equals(getValue(object, "msisdn"));
            }
        });
    }

    public static boolean isValidAlternateContactNumber(String alternateContactNumber) {
        return isValidWithBlanksAllowed(alternateContactNumber);
    }

    private static Object getValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
