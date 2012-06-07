package org.motechproject.ananya.referencedata.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationContract;

import static junit.framework.Assert.assertEquals;

public class LocationContractMapperTest {
    @Test
    public void shouldMapFromLocationToLocationContract() {
        String panchayat = "panchayat1";
        String block = "block1";
        String district = "district1";

        LocationContract locationContract = LocationContractMapper.mapFrom(new Location(district, block, panchayat));

        assertEquals(district, locationContract.getDistrict());
        assertEquals(block, locationContract.getBlock());
        assertEquals(panchayat, locationContract.getPanchayat());
    }
}
