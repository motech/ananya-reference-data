package org.motechproject.ananya.referencedata.flw.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.*;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerSyncRequestMapperTest {
    @Test
    public void shouldMapFromFlwToFlwContract() {
        String district = "District1";
        String block = "Block1";
        String panchayat = "Panchayat1";
        String state = "State1";
        DateTime now = DateTime.now();
        LocationStatus status = LocationStatus.VALID;
        Location location = new Location(state, district, block, panchayat, status, null);
        location.setLastModified(now);

        Long msisdn = 1234567890L;
        Long alternateContactNumber = 1234567890L;
        String name = "name1";
        Designation designation = Designation.ANM;
        NewMsisdn newMsisdn = new NewMsisdn("1234567899", null);

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, alternateContactNumber,name, designation, location, VerificationStatus.SUCCESS.name(), UUID.randomUUID(),null);
        frontLineWorker.setLastModified(now);
        frontLineWorker.setNewMsisdn(newMsisdn);
        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);

        assertEquals(msisdn.toString(), frontLineWorkerSyncRequest.getMsisdn());
        assertEquals(newMsisdn.msisdn().toString(), frontLineWorkerSyncRequest.getNewMsisdn());
        assertEquals(alternateContactNumber.toString(), frontLineWorkerSyncRequest.getAlternateContactNumber());
        assertEquals(name, frontLineWorkerSyncRequest.getName());
        assertEquals(designation.name(), frontLineWorkerSyncRequest.getDesignation());
        assertEquals(now, frontLineWorkerSyncRequest.getLastModified());

        LocationContract locationContract = frontLineWorkerSyncRequest.getLocation();
        assertEquals(block, locationContract.getBlock());
        assertEquals(district, locationContract.getDistrict());
        assertEquals(panchayat, locationContract.getPanchayat());
        assertEquals(state, locationContract.getState());
        assertEquals(frontLineWorker.getFlwId().toString(), frontLineWorkerSyncRequest.getFlwId());
        assertEquals(VerificationStatus.SUCCESS.name(), frontLineWorker.getVerificationStatus());
    }
}