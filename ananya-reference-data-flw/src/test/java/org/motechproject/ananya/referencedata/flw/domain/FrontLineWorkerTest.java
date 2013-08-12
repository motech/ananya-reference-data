package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class FrontLineWorkerTest {

    @Test
    public void shouldUpdateMsisdnOnChangeRequest() {
        FrontLineWorker flw = new FrontLineWorker();
        Long newMsisdn = 911234567890L;
        flw.setNewMsisdn(new NewMsisdn(newMsisdn.toString(), "2"));
        flw.updateToNewMsisdn();
        assertEquals(newMsisdn, flw.getMsisdn());
    }

    @Test
    public void shouldNotUpdateMsisdn() {
        FrontLineWorker flw = new FrontLineWorker();
        Long msisdn = 911234567890L;
        flw.setMsisdn(msisdn);
        flw.updateToNewMsisdn();
        assertEquals(msisdn, flw.getMsisdn());
    }

    @Test
    public void shouldGenerateUniqueFLWIdAndAssignOtherFields() {
        long msisdn = 9900503741L;
        String name = "flwname";
        Location location = new Location();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, Designation.ANM, location, VerificationStatus.SUCCESS.name());

        assertNotNull(frontLineWorker.getFlwId());
        assertEquals(msisdn, (long) frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(Designation.ANM.name(), frontLineWorker.getDesignation());
        assertEquals(location, frontLineWorker.getLocation());
    }

    @Test
    public void shouldCheckIfVerificationStatusExists() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker();
        frontLineWorker1.setVerificationStatus(VerificationStatus.INVALID);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker();

        assertTrue(frontLineWorker1.hasBeenVerified());
        assertFalse(frontLineWorker2.hasBeenVerified());

    }

    @Test
    public void shouldSetVerificationStatusToNullIfNullOrEmpty() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker();
        frontLineWorker1.setVerificationStatus(null);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker();
        frontLineWorker1.setVerificationStatus(VerificationStatus.from(""));

        assertTrue(frontLineWorker1.getVerificationStatus() == null);
        assertTrue(frontLineWorker2.getVerificationStatus() == null);
    }

    @Test
    public void shouldSetAlternateContactNumber() {
        Long alternateContactNumber = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(null, alternateContactNumber, null, null, null, null, null, null);
        assertEquals(alternateContactNumber, frontLineWorker.getAlternateContactNumber());
    }
}
