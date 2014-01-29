package org.motechproject.ananya.referencedata.flw.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FLWValidationUtilsTest {
    @Test
    public void shouldCheckNameWithBlankNameAllowed() {
        assertTrue(FLWValidationUtils.isInvalidNameWithBlankAllowed("A. b ' "));

        assertFalse(FLWValidationUtils.isInvalidNameWithBlankAllowed(null));

        assertFalse(FLWValidationUtils.isInvalidNameWithBlankAllowed("a. b S.  "));
    }

    @Test
    public void shouldCheckNameWithBlankNameNotAllowed() {
        assertTrue(FLWValidationUtils.isInvalidName("A. b ' "));

        assertTrue(FLWValidationUtils.isInvalidName(null));

        assertFalse(FLWValidationUtils.isInvalidName("a. b S.  "));
    }
}
