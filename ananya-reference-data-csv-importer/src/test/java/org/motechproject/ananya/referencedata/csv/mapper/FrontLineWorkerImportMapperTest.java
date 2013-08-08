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
        Location location = new Location("s", "d", "b", "p", LocationStatus.VALID, null);
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
        assertEquals(formatPhoneNumber(alternateContactNumber), frontLineWorker.getAlternateContactNumber());
        assertEquals(location, frontLineWorker.getLocation());
        assertNotNull(frontLineWorker.getFlwId());
        assertEquals(verificationStatus, frontLineWorker.getVerificationStatus());
        assertEquals("via CSV Upload", frontLineWorker.getReason());
    }

    @Test
    public void shouldMapBlankVerificationStatusAsNull() {
        Location location = new Location("s", "d", "b", "p", LocationStatus.VALID, null);
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, "1234567890", "1234567891",
                "name", "ANM", "", null);
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToNewFlw(request, location);
        assertNull(frontLineWorker.getVerificationStatus());
    }

    @Test
    public void shouldMapBlankAlternateContactNumberAsNull() {
        Location location = new Location("d", "b", "p", "s", LocationStatus.VALID, null);
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, "1234567890", "",
                "name", "ANM", "", null);
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToNewFlw(request, location);
        assertNull(frontLineWorker.getAlternateContactNumber());
    }

    @Test
    public void shouldMapRequestToExistingFLW() {
        Location location = new Location("s", "d", "b", "p", LocationStatus.VALID, null);
        String name = "name";
        String designation = "ANM";
        String verificationStatus = "SUCCESS";
        String alternateContactNumber = "1234567891";
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, null, alternateContactNumber,
                name, designation, verificationStatus, null);
        FrontLineWorker existingFLW = new FrontLineWorker();
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToExistingFlw(existingFLW, request, location);
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(location, frontLineWorker.getLocation());
        assertNull(frontLineWorker.getFlwId());
        assertNull(frontLineWorker.getMsisdn());
        assertEquals(formatPhoneNumber(alternateContactNumber), frontLineWorker.getAlternateContactNumber());
        assertEquals(verificationStatus, frontLineWorker.getVerificationStatus());
        assertEquals("via CSV Upload", frontLineWorker.getReason());
    }

    @Test
    public void shouldNotUpdateAlternateContactNumberIfItIsBlankInCSV() {
        Location location = new Location("d", "b", "p", "s", LocationStatus.VALID, null);
        String name = "name";
        String designation = "ANM";
        String verificationStatus = "SUCCESS";
        Long alternateContactNumber = 1234567L;
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, null, "",
                name, designation, verificationStatus, null);
        FrontLineWorker existingFLW = new FrontLineWorker();
        existingFLW.setAlternateContactNumber(alternateContactNumber);

        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToExistingFlw(existingFLW, request, location);
        assertEquals(alternateContactNumber, frontLineWorker.getAlternateContactNumber());
    }

    @Test
    public void shouldMapBlankVerificationStatusAsNullToExistingFLW() {
        Location location = new Location("s", "d", "b", "p", LocationStatus.VALID, null);
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, null, null,
                "name", "ANM", "", null);
        FrontLineWorker existingFLW = new FrontLineWorker();
        FrontLineWorker frontLineWorker = FrontLineWorkerImportMapper.mapToExistingFlw(existingFLW, request, location);
        assertNull("", frontLineWorker.getVerificationStatus());
    }
}
