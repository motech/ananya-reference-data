package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.UUID;

import static org.junit.Assert.*;

public class FrontLineWorkerVerificationRequestTest {

    private FrontLineWorkerVerificationRequest verificationRequest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        verificationRequest = new FrontLineWorkerVerificationRequest(UUID.fromString("11111111-1111-1111-1111-111111111111"), PhoneNumber.formatPhoneNumber("9900504646"), null, VerificationStatus.SUCCESS, null, null, null, null, null);
    }

    @Test
    public void shouldCheckIfDummyFLWId() {
        assertTrue(verificationRequest.isDummyFlwId());
    }

    @Test
    public void duplicateFlwIdThrowsExceptionWhenNoDuplicateExist() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Duplicate flw does not exist");
        verificationRequest.duplicateFlwId();
    }

    @Test
    public void shouldGetDuplicateFlwId() {
        String flwId = "2";
        verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), null, null, null
                , null, null, null, null, new ChangeMsisdnRequest("1", flwId));
        assertEquals(flwId, verificationRequest.duplicateFlwId());
    }

    @Test
    public void testHasMsisdnChange() {
        assertFalse(verificationRequest.hasMsisdnChange());
        verificationRequest = new FrontLineWorkerVerificationRequest(null, null, null, null, null,
                null, null, null, new ChangeMsisdnRequest(null, null));
        assertFalse(verificationRequest.hasMsisdnChange());
        verificationRequest = new FrontLineWorkerVerificationRequest(null, null, null, null,
                null, null, null, null, new ChangeMsisdnRequest("12", null));
        assertTrue(verificationRequest.hasMsisdnChange());
    }
}
