package org.motechproject.ananya.referencedata.flw.validators;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerCsvRequest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;

import static org.junit.Assert.*;

public class FrontLineWorkerCsvRequestValidatorTest {

    @Test
    public void shouldValidateMSISDNOfFrontLineWorker() {
        FrontLineWorkerCsvRequestValidator validator = new FrontLineWorkerCsvRequestValidator();

        FLWValidationResponse flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("msisdn", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("12345", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("123456789012", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage().toString());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("911234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("001234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("1256789031", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldValidateNameOfFrontLineWorker() {
        FrontLineWorkerCsvRequestValidator validator = new FrontLineWorkerCsvRequestValidator();

        FLWValidationResponse flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("1234567890", "Mr. Valid", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("1234567890", "Valid 1234", "ANM", new LocationRequest()), new Location());
        assertTrue(flwValidationResponse.isValid());

        flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("1234567890", "Invalid-Name", "ANM", new LocationRequest()), new Location());
        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateLocationOfFrontLineWorker() {
        FrontLineWorkerCsvRequestValidator validator = new FrontLineWorkerCsvRequestValidator();

        FLWValidationResponse flwValidationResponse = validator.validate(new FrontLineWorkerCsvRequest("1234567890", "Valid. Name", "ANM", new LocationRequest()), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage().toString());
    }
}
