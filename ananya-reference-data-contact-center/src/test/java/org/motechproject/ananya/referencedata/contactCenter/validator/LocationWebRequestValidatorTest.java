package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class LocationWebRequestValidatorTest {

    @Test
    public void shouldReturnErrorForAnInvalidLocationRequest() {
        Errors errors = LocationWebRequestValidator.validate(new LocationRequest(null, null, null));

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("district field is blank"));
        assertTrue(errors.hasMessage("block field is blank"));
        assertTrue(errors.hasMessage("panchayat field is blank"));
    }

    @Test
    public void shouldReturnErrorForANullLocationRequest() {
        Errors errors = LocationWebRequestValidator.validate(null);

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("location field is blank"));
    }

    @Test
    public void shouldNotContainErrorsForAValidLocationRequest() {
        Errors errors = LocationWebRequestValidator.validate(new LocationRequest("district", "block", "panchayat"));

        assertEquals(0, errors.getCount());
    }
}
