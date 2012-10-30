package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertTrue;

public class LocationStatusTest {

    @Test
    public void shouldReturnTheCorrectLocationStatus() {
        assertEquals(LocationStatus.VALID, LocationStatus.getFor(" valiD  "));
        assertEquals(LocationStatus.NOT_VERIFIED, LocationStatus.getFor("  Not_verified"));
        assertNull(LocationStatus.getFor("not verified"));
    }
}
