package org.motechproject.ananya.referencedata.flw.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest {
    @Test
    public void shouldCheckNameWithBlankNameAllowed() {
        assertTrue(ValidationUtils.isInvalidNameWithBlankAllowed("A. b ' "));

        assertFalse(ValidationUtils.isInvalidNameWithBlankAllowed(null));

        assertFalse(ValidationUtils.isInvalidNameWithBlankAllowed("a. b S.  "));
    }

    @Test
    public void shouldCheckNameWithBlankNameNotAllowed() {
        assertTrue(ValidationUtils.isInvalidName("A. b ' "));

        assertTrue(ValidationUtils.isInvalidName(null));

        assertFalse(ValidationUtils.isInvalidName("a. b S.  "));
    }
}
