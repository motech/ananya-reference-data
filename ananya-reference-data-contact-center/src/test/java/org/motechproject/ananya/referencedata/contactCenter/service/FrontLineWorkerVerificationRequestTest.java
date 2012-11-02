package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerVerificationRequestTest {

    @Test
    public void shouldInvalidateSuccessRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, null, null, null, null);
        Errors errors = verificationRequest.validate();
        assertEquals(3, errors.getCount());
        assertEquals("designation field has invalid/blank value,name field has invalid/blank value,location is missing", errors.allMessages());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfNameIsInvalid() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "कुछ", Designation.ASHA, new LocationRequest("district", "block", "panchayat"), null);
        Errors errors = verificationRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("name field has invalid/blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfLocationIsInvalid() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest(null, null, null), null);
        Errors errors = verificationRequest.validate();
        assertEquals(3, errors.getCount());
        assertEquals("district field is blank,block field is blank,panchayat field is blank", errors.allMessages());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfReasonIsFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("district", "block", "panchayat"), "");
        Errors errors = verificationRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("reason field should not be a part of the request", errors.allMessages());
    }

    @Test
    public void shouldInvalidateInvalidRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.INVALID, null, null, null, null);
        Errors errors = verificationRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("reason field has blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateInvalidRequestIfExtraFieldsAreFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.INVALID, "name", Designation.ASHA, new LocationRequest(), "some reason");
        Errors errors = verificationRequest.validate();
        assertEquals(3, errors.getCount());
        assertEquals("name field should not be a part of the request,location field should not be a part of the request,designation field should not be a part of the request", errors.allMessages());
    }

    @Test
    public void shouldInvalidateOtherRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.OTHER, null, null, null, null);
        Errors errors = verificationRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("reason field has blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateOtherRequestIfExtraFieldsAreFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.OTHER, "name", Designation.ASHA, new LocationRequest(), "some reason");
        Errors errors = verificationRequest.validate();
        assertEquals(3, errors.getCount());
        assertEquals("name field should not be a part of the request,location field should not be a part of the request,designation field should not be a part of the request", errors.allMessages());
    }
}
