package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class FrontLineWorkerVerificationRequestTest {
    @Test
    public void shouldCheckIfDummyFLWId() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.fromString("11111111-1111-1111-1111-111111111111"), PhoneNumber.formatPhoneNumber("9900504646"), null, VerificationStatus.SUCCESS, null, null, null, null, null);
        assertTrue(verificationRequest.isDummyFlwId());
    }
}
