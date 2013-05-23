package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerMapperTest {

    @Test
    public void shouldMapForAnUnsuccessfulFLWWebRequestToAnExistingFLW() {
        String reason = "random reason";
        Long msisdn = 12345678980L;
        String name = "name";
        Designation anm = Designation.ANM;
        Location location = new Location();
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        UUID flwId = UUID.randomUUID();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, name, anm, location, flwId, VerificationStatus.OTHER, null);

        FrontLineWorker newFrontLineWorker = FrontLineWorkerMapper.mapFrom(new FrontLineWorkerVerificationRequest(flwId, msisdn, verificationStatus, null, null, null, reason), existingFrontLineWorker);

        assertEquals(flwId, newFrontLineWorker.getFlwId());
        assertEquals(msisdn, newFrontLineWorker.getMsisdn());
        assertEquals(verificationStatus.name(), newFrontLineWorker.getVerificationStatus());
        assertEquals(reason, newFrontLineWorker.getReason());
    }

    @Test
    public void shouldMapASuccessfulFLWWebRequestToAnExistingFLW() {
        Long msisdn = 12345678980L;
        String name = "name";
        Designation designation = Designation.ANM;
        UUID flwId = UUID.randomUUID();
        LocationRequest location = new LocationRequest("state", "d", "b", "p", null);
        Location expectedLocation = LocationMapper.mapFrom(location);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, designation, new Location("d1", "b1", "p1", name, LocationStatus.NOT_VERIFIED, null), flwId, VerificationStatus.SUCCESS, null);
        String newName = "name123";
        Designation newDesignation = Designation.ASHA;
        FrontLineWorkerVerificationRequest request = new FrontLineWorkerVerificationRequest(flwId, msisdn, VerificationStatus.SUCCESS, newName, newDesignation, new LocationRequest("state", "d", "b", "p"), null);

        FrontLineWorker actualFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(request, frontLineWorker, expectedLocation);

        assertEquals(newName, actualFrontLineWorker.getName());
        assertEquals(newDesignation.name(), actualFrontLineWorker.getDesignation());
        assertEquals(expectedLocation, actualFrontLineWorker.getLocation());
    }
}
