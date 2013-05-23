package org.motechproject.ananya.referencedata.csv.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import static org.junit.Assert.*;

public class FrontLineWorkerImportRequestValidatorTest {

    @Test
    public void shouldValidateMSISDNOfFrontLineWorker() {
        FrontLineWorkerImportRequestValidator validator = new FrontLineWorkerImportRequestValidator();

        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("msisdn", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("12345", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("123456789012", "name", "ANM", new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("911234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("001234567890", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("1256789031", "name", "ANM", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());
    }

    @Test
    public void shouldValidateNameOfFrontLineWorker() {
        FrontLineWorkerImportRequestValidator validator = new FrontLineWorkerImportRequestValidator();

        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("1234567890", "Mr. Valid", "ANM", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("1234567890", "Valid 1234", "ANM", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("1234567890", "Invalid-Name", "ANM", new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid name]", frontLineWorkerImportValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateLocationOfFrontLineWorker() {
        FrontLineWorkerImportRequestValidator validator = new FrontLineWorkerImportRequestValidator();

        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("1234567890", "Valid. Name", "ANM", new LocationRequest()), null);

        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid location]", frontLineWorkerImportValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldUpdateLocationState() {
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest();
        String state = "Bihar";
        request.setState(state);

        assertEquals(state, request.getLocation().getState());
    }
}
