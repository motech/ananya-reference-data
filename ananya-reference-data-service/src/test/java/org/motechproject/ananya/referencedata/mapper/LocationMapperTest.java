package org.motechproject.ananya.referencedata.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.LocationRequest;

import static junit.framework.Assert.assertEquals;

public class LocationMapperTest {

    @Test
    public void shouldMapLocationRequestToLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";

        Location location = LocationMapper.mapFrom(new LocationRequest(district, block, panchayat));

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
    }
}
