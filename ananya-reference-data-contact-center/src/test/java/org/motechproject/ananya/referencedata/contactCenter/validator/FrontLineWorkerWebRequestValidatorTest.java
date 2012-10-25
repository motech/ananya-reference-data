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

    private UUID flwId = UUID.randomUUID();

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
        assertTrue(errors.hasMessage("name field is blank"));
        assertTrue(errors.hasMessage("designation field has invalid/blank value"));
        assertTrue(errors.hasMessage("location field is blank"));
    }
}