package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationTest {

    @Test
    public void shouldIgnoreCaseWhileComparingLocations() {
        Location location1 = new Location("d1", "B1", "p1", "NOT VERIFIED");
        Location location2 = new Location("D1", "b1", "p1", "NoT VERIFIED");
        assertEquals(location1, location2);
    }
}
