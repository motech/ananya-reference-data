package org.motechproject.ananya.referencedata.validators;

import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FLWValidationResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerValidatorTest {
    @Test
    public void shouldValidateMSISDNOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("msisdn", "name", "ANM", new LocationRequest()), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12345", "name", "ANM", new LocationRequest()), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12567890345", "name", "ANM", new LocationRequest()), new Location());

        assertTrue(flwValidationResponse.isValid());
    }
    
    @Test
    public void shouldValidateNameOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12345678901", "Mr. Valid", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12345678901", "Valid 1234", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12345678901", "Invalid-Name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateLocationOfFrontLineWorker() {
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateCreateRequest(new FrontLineWorkerRequest("12345678901", "Valid. Name", "ANM", new LocationRequest()), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage().toString());
    }
}
