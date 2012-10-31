package org.motechproject.ananya.referencedata.contactCenter.request;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerVerificationWebRequestTest {

    @Test
    public void shouldMandatoryFields() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(null, null, null, null, null, null, null);
        Errors errors = webRequest.validate();
        assertEquals(4, errors.getCount());
        assertEquals("id field is missing,msisdn field is missing,verificationStatus field is missing,channel is missing", errors.allMessages());
    }

    @Test
    public void shouldValidateValidRequestFields() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest("invalidflwid", "invalidmsisdn", "invalidverificationstatus", null, "invaliddesignation", null, "Some reason");
        webRequest.setChannel("invalid_channel");
        Errors errors = webRequest.validate();
        assertEquals(5, errors.getCount());
        assertEquals("id field is not in valid UUID format,msisdn field has invalid value,verificationStatus field has invalid value,invalid channel: invalid_channel,designation field has invalid value", errors.allMessages());
    }

    @Test
    public void shouldReturnVerificationRequest() {
        UUID flwId = UUID.randomUUID();
        Long msisdn = new Long("9900503456");
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        String name = "fwlName";
        String reason = "reason";
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(flwId.toString(), Long.toString(msisdn), "INVALID", name, Designation.ASHA.name(), locationRequest, reason);
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();

        assertEquals(flwId, verificationRequest.getFlwId());
        assertEquals(msisdn, verificationRequest.getMsisdn());
        assertEquals(Designation.ASHA, verificationRequest.getDesignation());
        assertEquals(VerificationStatus.INVALID, verificationRequest.getVerificationStatus());
        assertEquals(locationRequest, verificationRequest.getLocation());
        assertEquals(name, verificationRequest.getName());
    }
}
