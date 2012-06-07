package org.motechproject.ananya.referencedata.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.*;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerContractMapperTest {
    @Test
    public void shouldMapFromFlwToFlwContract() {
        String district = "district1";
        String block = "block1";
        String panchayat = "panchayat1";
        Location location = new Location(district, block, panchayat);

        Long msisdn = 1234567890L;
        String name = "name1";
        Designation designation = Designation.ANM;

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, name, designation, location);
        FrontLineWorkerContract frontLineWorkerContract = FrontLineWorkerContractMapper.mapFrom(frontLineWorker);

        assertEquals(msisdn.toString(), frontLineWorkerContract.getMsisdn());
        assertEquals(name, frontLineWorkerContract.getName());
        assertEquals(designation.name(), frontLineWorkerContract.getDesignation());

        LocationContract locationContract = frontLineWorkerContract.getLocation();
        assertEquals(block, locationContract.getBlock());
        assertEquals(district, locationContract.getDistrict());
        assertEquals(panchayat, locationContract.getPanchayat());
    }
}
