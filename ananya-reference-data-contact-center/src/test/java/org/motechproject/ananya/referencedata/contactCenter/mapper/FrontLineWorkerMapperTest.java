package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
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
        String verificationStatus = VerificationStatus.INVALID.name();
        UUID flwId = UUID.randomUUID();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, name, anm, location, flwId, VerificationStatus.OTHER, null);
        FrontLineWorker newFrontLineWorker = FrontLineWorkerMapper.mapFrom(new FrontLineWorkerWebRequest(flwId, msisdn.toString(), verificationStatus, reason), existingFrontLineWorker);

        assertEquals(flwId, newFrontLineWorker.getFlwId());
        assertEquals(msisdn, newFrontLineWorker.getMsisdn());
        assertEquals(verificationStatus, newFrontLineWorker.getVerificationStatus());
        assertEquals(reason, newFrontLineWorker.getReason());
    }

    @Test
    public void shouldMapASuccessfulFLWWebRequestToAnExistingFLW() {
        Long msisdn = 12345678980L;
        String name = "name";
        Designation designation = Designation.ANM;
        UUID flwId = UUID.randomUUID();
        LocationRequest location = new LocationRequest("d", "b", "p", null);
        Location expectedLocation = LocationMapper.mapFrom(location);

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, designation, expectedLocation, flwId, VerificationStatus.SUCCESS, null);
        FrontLineWorker actualFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(frontLineWorker, expectedLocation);

        assertEquals(frontLineWorker,actualFrontLineWorker);
    }
}
