package org.motechproject.ananya.referencedata.flw.utils;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import static junit.framework.Assert.*;

public class PhoneNumberTest {
    @Test
    public void shouldFailValidationForNullOrEmpty() {
        assertFalse(PhoneNumber.isValid(""));
        assertFalse(PhoneNumber.isValid(null));
    }

    @Test
    public void shouldFailValidationForPhoneNumberLessThanOrGreaterThan10Digits() {
        assertFalse(PhoneNumber.isValid("12"));
        assertFalse(PhoneNumber.isValid("123456789012312"));
    }

    @Test
    public void shouldFailValidationIfTheNumberContainsInvalidCharacters() {
        assertFalse(PhoneNumber.isValid("test1123123-"));
    }

    @Test
    public void shouldPassValidationIfPhoneNumberIs12DigitButStartsWith00Or91() {
        assertTrue(PhoneNumber.isValid("911234567890"));
        assertTrue(PhoneNumber.isValid("001234567890"));
    }

    @Test
    public void shouldPassValidationFor10DigitPhoneNumber() {
        assertTrue(PhoneNumber.isValid("1234567890"));
    }

    @Test
    public void shouldAppend91To12DigitPhoneNumbersAnd10DigitPhoneNumbers() {
        PhoneNumber phoneNumber = new PhoneNumber("1234567890");
        assertEquals(new Long(911234567890L), phoneNumber.getPhoneNumber());

        phoneNumber = new PhoneNumber("001234567890");
        assertEquals(new Long(911234567890L), phoneNumber.getPhoneNumber());
    }

    @Test
    public void shouldReturnNullIfNumberIsNotInTheRightFormat() {
        PhoneNumber invalidPhoneNumber = new PhoneNumber("invalidPhoneNumber");
        assertNull(invalidPhoneNumber.getPhoneNumber());
    }

    @Test
    public void shouldPassValidationForBlanks() {
        assertTrue(PhoneNumber.isValidWithBlanksAllowed(null));
        assertTrue(PhoneNumber.isValidWithBlanksAllowed(""));
    }
}
