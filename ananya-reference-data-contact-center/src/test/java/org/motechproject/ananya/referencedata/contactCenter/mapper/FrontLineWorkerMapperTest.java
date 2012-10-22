package org.motechproject.ananya.referencedata.contactCenter.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerMapperTest {

    @Test
    public void shouldMapFormFLWWebRequestAndExisingFLW() {
        String reason = "random reason";
        Long msisdn = 12345678980L;
        String name = "name";
        Designation anm = Designation.ANM;
        Location location = new Location();
        String verificationStatus = VerificationStatus.INVALID.name();
        String guid = "guid";
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(msisdn, name, anm, location, guid, VerificationStatus.OTHERS, null);
        FrontLineWorker newFrontLineWorker = FrontLineWorkerMapper.mapFrom(new FrontLineWorkerWebRequest(guid, verificationStatus, reason), existingFrontLineWorker);

        assertEquals(guid, newFrontLineWorker.getFlwGuid());
        assertEquals(msisdn, newFrontLineWorker.getMsisdn());
        assertEquals(verificationStatus, newFrontLineWorker.getVerificationStatus());
        assertEquals(reason, newFrontLineWorker.getReason());
    }
}
