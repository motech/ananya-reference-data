package org.motechproject.ananya.referencedata.csv.request;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationImportRequestTest {
    @Test
    public void shouldValidateRequestInvalidStatus() {
        LocationImportRequest request = new LocationImportRequest("d", "b", "p", LocationStatus.INVALID.name());
        assertTrue(request.isForInvalidation());

        request.setStatus(LocationStatus.VALID.name());
        assertFalse(request.isForInvalidation());
    }

    @Test
    public void shouldValidateExistanceOfAlternateLocation() {
        LocationImportRequest request = new LocationImportRequest("d", "b", "p", LocationStatus.INVALID.name());
        assertFalse(request.hasAlternateLocation());

        request = new LocationImportRequest("d", "b", "p", LocationStatus.INVALID.name(), "d1", "b1", "p1");
        assertTrue(request.hasAlternateLocation());
    }
}
