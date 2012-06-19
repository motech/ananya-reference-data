package org.motechproject.ananya.referencedata.flw.validators;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerValidatorTest {
    @Test
    public void shouldValidateMSISDNOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("msisdn", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("12345", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("123456789012", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("911234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("001234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("1256789031", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldValidateNameOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", "Mr. Valid", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", "Valid 1234", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", "Invalid-Name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateLocationOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", "Valid. Name", "ANM", new LocationRequest()), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage().toString());
    }
}
