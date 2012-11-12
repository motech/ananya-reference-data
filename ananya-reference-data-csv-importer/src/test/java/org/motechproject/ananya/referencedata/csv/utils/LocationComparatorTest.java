package org.motechproject.ananya.referencedata.csv.utils;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LocationComparatorTest {

    @Test
    public void shouldSortAllTheValidLocationsToStartOfFile() {
        final LocationImportCSVRequest locationImportCSVRequest1 = locationImportCSVRequest("d", "b", "p", LocationStatus.INVALID.getDescription());
        final LocationImportCSVRequest locationImportCSVRequest2 = locationImportCSVRequest("d1", "b1", "p1", LocationStatus.VALID.getDescription());
        final LocationImportCSVRequest locationImportCSVRequest3 = locationImportCSVRequest("d1", "b1", "p2", LocationStatus.VALID.getDescription());
        final LocationImportCSVRequest locationImportCSVRequest4 = locationImportCSVRequest("d2", "b2", "p2", LocationStatus.NEW.getDescription());
        List<LocationImportCSVRequest> locationImportCSVRequests = new ArrayList<LocationImportCSVRequest>() {{
            add(locationImportCSVRequest1);
            add(locationImportCSVRequest2);
            add(locationImportCSVRequest3);
            add(locationImportCSVRequest4);
        }};

        Collections.sort(locationImportCSVRequests, new LocationComparator());

        assertEquals(locationImportCSVRequest2, locationImportCSVRequests.get(0));
        assertEquals(locationImportCSVRequest3, locationImportCSVRequests.get(1));
        assertEquals(locationImportCSVRequest4, locationImportCSVRequests.get(2));
        assertEquals(locationImportCSVRequest1, locationImportCSVRequests.get(3));
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(district, block, panchayat, status);
    }
}