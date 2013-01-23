package org.motechproject.ananya.referencedata.flw.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import static junit.framework.Assert.assertEquals;

public class LocationMapperTest {

    @Test
    public void shouldMapLocationRequestToLocation() {
        String district = "District";
        String block = "Block";
        String panchayat = "Panchayat";

        Location location = LocationMapper.mapFrom(new LocationRequest(district, block, panchayat));

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals(LocationStatus.NOT_VERIFIED, location.getStatus());
    }
}
