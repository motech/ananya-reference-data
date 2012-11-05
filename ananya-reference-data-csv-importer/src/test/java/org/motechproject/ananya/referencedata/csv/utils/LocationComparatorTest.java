package org.motechproject.ananya.referencedata.csv.utils;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class LocationComparatorTest {

    @Test
    public void shouldSortAllTheValidLocationsToStartOfFile() {
        final LocationImportRequest locationImportRequest1 = new LocationImportRequest("d", "b", "p", LocationStatus.INVALID.toString());
        final LocationImportRequest locationImportRequest2 = new LocationImportRequest("d1", "b1", "p1", LocationStatus.VALID.toString());
        final LocationImportRequest locationImportRequest3 = new LocationImportRequest("d1", "b1", "p2", LocationStatus.VALID.toString());
        final LocationImportRequest locationImportRequest4 = new LocationImportRequest("d2", "b2", "p2", LocationStatus.NEW.toString());
        List<LocationImportRequest> locationImportRequests = new ArrayList<LocationImportRequest>() {{
            add(locationImportRequest1);
            add(locationImportRequest2);
            add(locationImportRequest3);
            add(locationImportRequest4);
        }};

        Collections.sort(locationImportRequests, new LocationComparator());

        assertEquals(locationImportRequest2, locationImportRequests.get(0));
        assertEquals(locationImportRequest3, locationImportRequests.get(1));
        assertEquals(locationImportRequest4, locationImportRequests.get(2));
        assertEquals(locationImportRequest1, locationImportRequests.get(3));
    }
}
