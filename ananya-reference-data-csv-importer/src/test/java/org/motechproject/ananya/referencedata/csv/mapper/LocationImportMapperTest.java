package org.motechproject.ananya.referencedata.csv.mapper;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import static junit.framework.Assert.assertEquals;

public class LocationImportMapperTest {
    @Test
    public void shouldMapLocationImportRequestToLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";

        Location location = LocationImportMapper.mapFrom(new LocationImportRequest(district, block, panchayat));

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals(LocationStatus.VALID.name(), location.getStatus());
    }

    @Test
    public void shouldMapLocationStatusOnlyForAnExistingLocation() {
        String district = "d1";
        String block = "b1";
        String panchayat = "p1";
        Location location = LocationImportMapper.mapFrom(new Location(district, block, panchayat, "", null),
                new LocationImportRequest(district, block, panchayat, "in_review"));

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals(LocationStatus.IN_REVIEW.name(), location.getStatus());
    }
}
