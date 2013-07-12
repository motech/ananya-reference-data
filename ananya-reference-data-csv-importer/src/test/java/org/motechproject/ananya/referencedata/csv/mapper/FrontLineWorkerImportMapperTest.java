package org.motechproject.ananya.referencedata.csv.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class FrontLineWorkerImportMapperTest {

    @Test
    public void shouldMapRequestToFLW() {
        Location location = new Location("d", "b", "p", "s", LocationStatus.VALID, null);
        String name = "name";
        String designation = "ANM";
        String msisdn = "1234567890";
        String alternateContactNumber = "1234567891";
        String verificationStatus = "SUCCESS";
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, msisdn, alternateContactNumber,
                name, designation, verificationStatus, null);
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToNewFlw(request, location);
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(formatPhoneNumber(msisdn), frontLineWorker.getMsisdn());
        assertEquals(location, frontLineWorker.getLocation());
        assertNotNull(frontLineWorker.getFlwId());
        assertEquals(verificationStatus, frontLineWorker.getVerificationStatus());
    }

    @Test
    public void shouldMapRequestToExistingFLW() {
        Location location = new Location("d", "b", "p", "s", LocationStatus.VALID, null);
        String name = "name";
        String designation = "ANM";
        String verificationStatus = "SUCCESS";
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, null, null,
                name, designation, verificationStatus, null);
        FrontLineWorker existingFLW = new FrontLineWorker();
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToExistingFlw(existingFLW,request, location);
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(location, frontLineWorker.getLocation());
        assertNull(frontLineWorker.getFlwId());
        assertNull(frontLineWorker.getMsisdn());
        assertNull(frontLineWorker.getReason());
        assertEquals(verificationStatus, frontLineWorker.getVerificationStatus());
    }
}
