package org.motechproject.ananya.referencedata.contactCenter.request;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FrontLineWorkerVerificationWebRequestTest {

    @Test
    public void shouldHandleMandatoryFields() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(null, null, null, null, null, null, null, null);
        Errors errors = webRequest.validate();
        assertEquals(4, errors.getCount());
        assertEquals("id field is missing,msisdn field is missing,verificationStatus field is missing,channel is missing", errors.allMessages());
    }

    @Test
    public void shouldValidateInvalidValidRequestFields() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest("invalidflwid", "invalidmsisdn", null, "invalidverificationstatus", null, "invaliddesignation", null, "Some reason");
        webRequest.setChannel("invalid_channel");
        Errors errors = webRequest.validate();
        assertEquals(5, errors.getCount());
        assertEquals("id field is not in valid UUID format,msisdn field has invalid value,verificationStatus field has invalid value,invalid channel: invalid_channel,designation field has invalid value", errors.allMessages());
    }

    @Test
    public void shouldValidateValidRequest() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(UUID.randomUUID().toString(), "9900502341", null, "SUCCESS", "name", "ASHA", null, null);
        webRequest.setChannel("contact_center");
        assertFalse(webRequest.validate().hasErrors());
    }


    @Test
    public void shouldNotAllowPrefixForMsisdn() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(UUID.randomUUID().toString(), "009900502341", null, "SUCCESS", "name", "ASHA", null, null);
        webRequest.setChannel("contact_center");
        Errors errors = webRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("msisdn field has invalid value", errors.allMessages());
    }

    @Test
    public void shouldNotAllowPrefixForAlternateContactNumber() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(UUID.randomUUID().toString(), "9900502341", "009900502341", "SUCCESS", "name", "ASHA", null, null);
        webRequest.setChannel("contact_center");
        Errors errors = webRequest.validate();
        assertEquals(1, errors.getCount());
        assertEquals("alternate_contact_number field has invalid value", errors.allMessages());
    }

    @Test
    public void shouldAllowBlankForAlternateContactNumber() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(UUID.randomUUID().toString(), "9900502341", "", "SUCCESS", "name", "ASHA", null, null);
        webRequest.setChannel("contact_center");
        Errors errors = webRequest.validate();
        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldReturnVerificationRequest() {
        UUID flwId = UUID.randomUUID();
        Long msisdn = new Long("9900503456");
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat", "state");
        String name = "fwlName";
        String reason = "reason";
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(flwId.toString(), Long.toString(msisdn), null, "INVALID", name, Designation.ASHA.name(), locationRequest, reason);
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();

        Long expectedMsisdn = 919900503456L;

        assertEquals(flwId, verificationRequest.getFlwId());
        assertEquals(expectedMsisdn, verificationRequest.getMsisdn());
        assertEquals(Designation.ASHA, verificationRequest.getDesignation());
        assertEquals(VerificationStatus.INVALID, verificationRequest.getVerificationStatus());
        assertEquals(locationRequest, verificationRequest.getLocation());
        assertEquals(name, verificationRequest.getName());
    }

    @Test
    public void shouldReturnVerificationRequestForMsisdnHaving91AsPrefix() {
        UUID flwId = UUID.randomUUID();
        Long msisdn = new Long("919900503456");
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat", "state");
        String name = "fwlName";
        String reason = "reason";
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequest(flwId.toString(), Long.toString(msisdn), null, "INVALID", name, Designation.ASHA.name(), locationRequest, reason);
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();

        Long expectedMsisdn = 919900503456L;
        assertEquals(expectedMsisdn, verificationRequest.getMsisdn());
    }

    @Test
    public void shouldUseBiharIfStateFieldIsMissing() {
        FrontLineWorkerVerificationWebRequest webRequest = FrontLineWorkerVerificationWebRequestBuilder.requestWithState(null);
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();
        assertEquals("Bihar", verificationRequest.getLocation().getState());
    }

    @Test
    public void shouldUseStateWhenProvided() {
        FrontLineWorkerVerificationWebRequest webRequest = FrontLineWorkerVerificationWebRequestBuilder.requestWithState("Orissa");
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();
        assertEquals("Orissa", verificationRequest.getLocation().getState());
    }

    @Test
    public void shouldNotHandleBlankValueForState() {
        FrontLineWorkerVerificationWebRequest webRequest = FrontLineWorkerVerificationWebRequestBuilder.requestWithState("");
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();
        assertEquals("", verificationRequest.getLocation().getState());
    }

    @Test
    public void shouldHandleBlankAlternateContactNumber() {
        FrontLineWorkerVerificationWebRequest webRequest = new FrontLineWorkerVerificationWebRequestBuilder().
                withFlwId(FrontLineWorker.DEFAULT_UUID_STRING).
                withMsisdn("1234567890").
                withAlternateContactNumber("   ").build();
        FrontLineWorkerVerificationRequest verificationRequest = webRequest.getVerificationRequest();
        assertNull(verificationRequest.getAlternateContactNumber());
    }
}
