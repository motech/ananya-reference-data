package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class FrontLineWorkerTest {

    @Test
    public void shouldGenerateUniqueFLWIdAndAssignOtherFields() {
        long msisdn = 9900503741L;
        String name = "flwname";
        Location location = new Location();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, Designation.ANM, location);

        assertNotNull(frontLineWorker.getFlwId());
        assertEquals(msisdn, (long) frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(Designation.ANM.name(), frontLineWorker.getDesignation());
        assertEquals(location, frontLineWorker.getLocation());
    }

    @Test
    public void shouldCheckIfVerificationStatusExists() {
        FrontLineWorker frontLineWorker1= new FrontLineWorker();
        frontLineWorker1.setVerificationStatus(VerificationStatus.INVALID);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker();

        assertTrue(frontLineWorker1.hasBeenVerified());
        assertFalse(frontLineWorker2.hasBeenVerified());

    }
}
