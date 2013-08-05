package org.motechproject.ananya.referencedata.flw.request;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocationRequestTest {
    @Test
    public void shouldUseBiharIfStateIsNotProvided() {
        LocationRequest locationRequest = new LocationRequest(null, null, null, null);
        assertNull(locationRequest.getState());
        locationRequest.handleMissingState();
        assertEquals("Bihar", locationRequest.getState());
    }
}
