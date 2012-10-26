package org.motechproject.ananya.referencedata.web.validator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WebRequestValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldInvalidateAndThrowExceptionIfChannelIsInvalid() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();

        Errors errors = webRequestValidator.validateChannel("invalid_channel");

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertEquals("Invalid channel: invalid_channel", errors.allMessages());
    }
}
