package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FrontLineWorkerWebRequestValidatorTest {

    @Test
    public void shouldInvalidateForMissingMandatoryFields() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest(null, null, null));

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("Guid field is blank"));
        assertTrue(errors.hasMessage("Verification-Status field has invalid/blank value"));
        assertTrue(errors.hasMessage("Reason field is blank"));
    }

    @Test
    public void shouldThrowErrorForInvalidStatus() {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest("guid", "wooster", "bertie");
        Errors errors = FrontLineWorkerWebRequestValidator.validate(frontLineWorkerWebRequest);
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Verification-Status field has invalid/blank value"));
    }

    @Test
    public void shouldValidateAValidRequest() {
        Errors errors = FrontLineWorkerWebRequestValidator.validate(new FrontLineWorkerWebRequest("guid", VerificationStatus.INVALID.name(), "reason"));
        assertEquals(0, errors.getCount());
    }
}