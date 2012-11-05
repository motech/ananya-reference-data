package org.motechproject.ananya.referencedata.flw.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.*;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerSyncRequestMapperTest {
    @Test
    public void shouldMapFromFlwToFlwContract() {
        String district = "district1";
        String block = "block1";
        String panchayat = "panchayat1";
        DateTime now = DateTime.now();
        LocationStatus status = LocationStatus.VALID;
        Location location = new Location(district, block, panchayat, status, null);
        location.setLastModified(now);

        Long msisdn = 1234567890L;
        String name = "name1";
        Designation designation = Designation.ANM;

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, designation, location);
        frontLineWorker.setLastModified(now);
        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);

        assertEquals(msisdn.toString(), frontLineWorkerSyncRequest.getMsisdn());
        assertEquals(name, frontLineWorkerSyncRequest.getName());
        assertEquals(designation.name(), frontLineWorkerSyncRequest.getDesignation());
        assertEquals(now.toDate(), frontLineWorkerSyncRequest.getLastModified());

        LocationContract locationContract = frontLineWorkerSyncRequest.getLocation();
        assertEquals(block, locationContract.getBlock());
        assertEquals(district, locationContract.getDistrict());
        assertEquals(panchayat, locationContract.getPanchayat());
        assertEquals(frontLineWorker.getFlwId().toString(), frontLineWorkerSyncRequest.getFlwId());
    }
}