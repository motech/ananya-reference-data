package org.motechproject.ananya.referencedata.flw.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FrontLineWorkerContractMapperTest {
    @Test
    public void shouldMapFromFlwToFlwContract() {
        String district = "district1";
        String block = "block1";
        String panchayat = "panchayat1";
        DateTime now = DateTime.now();
        Location location = new Location(district, block, panchayat);
        location.setLastModified(now);

        Long msisdn = 1234567890L;
        String name = "name1";
        Designation designation = Designation.ANM;

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, designation, location);
        frontLineWorker.setLastModified(now);
        FrontLineWorkerContract frontLineWorkerContract = FrontLineWorkerContractMapper.mapFrom(frontLineWorker);

        assertEquals(msisdn.toString(), frontLineWorkerContract.getMsisdn());
        assertEquals(name, frontLineWorkerContract.getName());
        assertEquals(designation.name(), frontLineWorkerContract.getDesignation());
        assertEquals(now.toDate(), frontLineWorkerContract.getLastModified());

        LocationContract locationContract = frontLineWorkerContract.getLocation();
        assertEquals(block, locationContract.getBlock());
        assertEquals(district, locationContract.getDistrict());
        assertEquals(panchayat, locationContract.getPanchayat());

        assertEquals(frontLineWorker.getFlwid(), frontLineWorkerContract.getFlwId());
    }
}
