package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FrontLineWorkerWebRequestValidatorTest {

    @Test
    public void shouldInvalidateForMissingMandatoryFields() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(null, null, null));

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("id field is blank"));
        assertTrue(errors.hasMessage("verificationStatus field has invalid/blank value"));
        assertTrue(errors.hasMessage("reason field is blank"));
    }

    @Test
    public void shouldThrowErrorForInvalidStatus() {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest("flwId", "wooster", "bertie");
        Errors errors = FrontLineWorkerWebRequestValidator.validate(frontLineWorkerWebRequest);
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("verificationStatus field has invalid/blank value"));
    }

    @Test
    public void shouldAllowCaseInsensitiveVerificationStatus() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest("flwId", "invaliD", "reason"));
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldAllowVerificationStatusWithSpaces() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest("flwId", "  InvaliD ", "reason"));
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateAValidRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest("flwId", VerificationStatus.INVALID.name(), "reason"));
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldValidateASuccessfulFLWRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest("flwId", VerificationStatus.SUCCESS.name(),
                "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat", null)));

        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldThrowErrorIfAnFaultySuccessfulFLWIsPosted() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(null, VerificationStatus.SUCCESS.name(), null, null, null));

        assertEquals(4, errors.getCount());
        assertTrue(errors.hasMessage("id field is blank"));
        assertTrue(errors.hasMessage("name field is blank"));
        assertTrue(errors.hasMessage("designation field has invalid/blank value"));
        assertTrue(errors.hasMessage("location field is blank"));
    }
}