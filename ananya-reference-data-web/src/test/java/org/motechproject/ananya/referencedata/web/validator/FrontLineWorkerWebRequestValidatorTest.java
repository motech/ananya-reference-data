package org.motechproject.ananya.referencedata.web.validator;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerWebRequestValidatorTest {

    @Test
    public void shouldReturnErrorIfReasonDoesNotExist() {
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest("guid", VerificationStatus.INVALID.name(), null);
        Errors errors = new FrontLineWorkerWebRequestValidator().validateFrontLineWorkerRequest(frontLineWorkerRequest);
        assertTrue(errors.hasErrors());
        assertEquals(1,errors.getCount());
        assertTrue(errors.hasMessage("Reason field has invalid/blank value"));
    }

    @Test
    public void shouldReturnErrorsIfRequiredParametersAreNull() {
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(null, null, null);
        Errors errors = new FrontLineWorkerWebRequestValidator().validateFrontLineWorkerRequest(frontLineWorkerRequest);
        assertTrue(errors.hasErrors());
        assertEquals(3,errors.getCount());
        assertTrue(errors.hasMessage("Reason field has invalid/blank value"));
        assertTrue(errors.hasMessage("Guid field has invalid/blank value"));
        assertTrue(errors.hasMessage("Verification-Status field has invalid/blank value"));

    }
}
