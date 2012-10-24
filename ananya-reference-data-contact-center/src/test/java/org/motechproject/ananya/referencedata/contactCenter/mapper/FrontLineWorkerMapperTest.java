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
        String flwId = UUID.randomUUID().toString();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, name, anm, location, flwId, VerificationStatus.OTHERS, null);
        FrontLineWorker newFrontLineWorker = FrontLineWorkerMapper.mapUnsuccessfulRegistration(new FrontLineWorkerWebRequest(flwId, verificationStatus, reason), existingFrontLineWorker);

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
        String verificationStatus = VerificationStatus.SUCCESS.name();
        String flwId = UUID.randomUUID().toString();
        LocationRequest location = new LocationRequest("d", "b", "p", null);
        Location expectedLocation = LocationMapper.mapFrom(location);

        FrontLineWorker expectedFrontLineWorker = new FrontLineWorker(msisdn, name, designation, expectedLocation, flwId, VerificationStatus.SUCCESS, null);
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, "Tom&Jerry", null, null, flwId, VerificationStatus.OTHERS, "oldreason");
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, verificationStatus, name, designation.name(), location);
        FrontLineWorker actualFrontLineWorker = FrontLineWorkerMapper.mapSuccessfulRegistration(frontLineWorkerWebRequest, existingFrontLineWorker, expectedLocation);

        assertEquals(expectedFrontLineWorker,actualFrontLineWorker);
    }
}
