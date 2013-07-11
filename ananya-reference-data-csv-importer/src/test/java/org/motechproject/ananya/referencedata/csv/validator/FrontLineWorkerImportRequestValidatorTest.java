package org.motechproject.ananya.referencedata.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

public class FrontLineWorkerImportRequestValidatorTest {

    private FrontLineWorkerImportRequestValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new FrontLineWorkerImportRequestValidator();
    }

    @Test
    public void shouldValidateMSISDN() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "msisdn", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "12345", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "123456789012", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "911234567890", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "001234567890", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1256789031", "1234567891", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());
    }

    @Test
    public void shouldValidateName() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567890", "1234567891", "Mr. Valid", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567890", "1234567891", "Valid 1234", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567890", "1234567891", "Invalid-Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid name]", frontLineWorkerImportValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateLocation() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), null);

        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid location]", frontLineWorkerImportValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateId() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(null, "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("", "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("11111111-1111-1111-1111-111111111111", "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest("11111111-1111-1111-1111-1111", "1234567890", "1234567891", "Valid. Name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid id]", frontLineWorkerImportValidationResponse.getMessage().toString());
    }

    @Test
    public void shouldValidateAlternateContactNumber() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "msisdn", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid alternate contact number]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "12345", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid alternate contact number]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "123456789012", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid alternate contact number]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "911234567890", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "001234567890", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "1256789031", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", "", "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", null, "name", "ANM", VerificationStatus.SUCCESS.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());
    }

    @Test
    public void shouldValidateVerificationStatus() {
        FrontLineWorkerImportValidationResponse frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", null, "name", "ANM", "Foo", new LocationRequest()), new Location());
        assertFalse(frontLineWorkerImportValidationResponse.isValid());
        assertEquals("[Invalid verification status]", frontLineWorkerImportValidationResponse.getMessage().toString());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", null, "name", "ANM", VerificationStatus.INVALID.name(), new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", null, "name", "ANM", "", new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());

        frontLineWorkerImportValidationResponse = validator.validate(new FrontLineWorkerImportRequest(randomUUID().toString(), "1234567891", null, "name", "ANM", null, new LocationRequest()), new Location());
        assertTrue(frontLineWorkerImportValidationResponse.isValid());
    }
}
