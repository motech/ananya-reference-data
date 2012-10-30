package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FrontLineWorkerWebRequestValidatorTest {

    private String flwId = UUID.randomUUID().toString();

    @Test
    public void shouldInvalidateMissingIdAndVerificationStatusAndNotOtherFields() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(null, null, null, null));

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("id field is blank"));
        assertTrue(errors.hasMessage("msisdn field has invalid/blank value"));
        assertTrue(errors.hasMessage("verificationStatus field has invalid/blank value"));
    }

    @Test
    public void shouldInvalidateMissingFieldsInUnsuccessfulRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(flwId, "9234567890", VerificationStatus.OTHER.name(), null));

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("reason field is blank"));
    }

    @Test
    public void shouldInvalidateTheFlwIdFormat(){
        String uuidInInvalidFormat = "abcd1234";

        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(uuidInInvalidFormat, "9234567890", VerificationStatus.OTHER.name(), "reason"));

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("id field is not in valid UUID format"));
    }

    @Test
    public void shouldInvalidateTheFlwIdWithExtraCharacters(){
        String uuidWithExtraCharacters = "1234dadb-1234-1234-9876-abcdeef2345689";

        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(uuidWithExtraCharacters, "9234567890", VerificationStatus.OTHER.name(), "reason"));

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("id field is not in valid UUID format"));
    }

    @Test
    public void shouldInvalidateFlwIdWithInvalidCharacters(){
        String uuidWithInvalidCharacters = "1234dadb-1234-1234-9876-abcdef1234xy";

        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(uuidWithInvalidCharacters, "9234567890", VerificationStatus.OTHER.name(), "reason"));

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("id field is not in valid UUID format"));
    }

    @Test
    public void shouldThrowErrorForInvalidStatus() {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "923456789", "wooster", "bertie");

        Errors errors = FrontLineWorkerWebRequestValidator.validate(frontLineWorkerWebRequest);

        assertEquals(2, errors.getCount());
        assertTrue(errors.hasMessage("msisdn field has invalid/blank value"));
        assertTrue(errors.hasMessage("verificationStatus field has invalid/blank value"));
    }

    @Test
    public void shouldValidateAValidRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(flwId, "9234567890", VerificationStatus.INVALID.name(), "reason"));

        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateASuccessfulFLWRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(flwId, "9234567890", VerificationStatus.SUCCESS.name(),
                "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat", null)));

        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldThrowErrorIfAFaultySuccessfulFLWWithIdIsPosted() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(flwId, "9234567890", VerificationStatus.SUCCESS.name(), null, null, null));

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("name field has invalid/blank value"));
        assertTrue(errors.hasMessage("designation field has invalid/blank value"));
        assertTrue(errors.hasMessage("location field is blank"));
    }

    @Test
    public void shouldInvalidateNameWithInvalidCharactersForASuccessfulRegistration(){
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(flwId, "9234567890", VerificationStatus.SUCCESS.name(), "a, b * c", "ANM", null));

        assertEquals(2, errors.getCount());
        assertTrue(errors.hasMessage("name field has invalid/blank value"));
        assertTrue(errors.hasMessage("location field is blank"));
    }
}