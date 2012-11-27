package org.motechproject.ananya.referencedata.flw.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationContract;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class LocationContractMapperTest {
    @Test
    public void shouldMapFromLocationToLocationContract() {
        String panchayat = "panchayat1";
        String block = "block1";
        String district = "district1";

        Location location = new Location(district, block, panchayat, LocationStatus.VALID, null);
        DateTime lastModified = DateTime.now();
        location.setLastModified(lastModified);
        LocationContract locationContract = LocationContractMapper.mapFrom(location);

        assertEquals(district, locationContract.getDistrict());
        assertEquals(block, locationContract.getBlock());
        assertEquals(panchayat, locationContract.getPanchayat());
    }

    @Test
    public void shouldPermitNullLocation() {
        assertNull(LocationContractMapper.mapFrom(null));
    }
}
