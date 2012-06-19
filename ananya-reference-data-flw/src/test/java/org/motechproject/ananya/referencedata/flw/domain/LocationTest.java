package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationTest {

    @Test
    public void shouldIgnoreCaseWhileComparingLocations() {
        Location location1 = new Location("d1", "B1", "p1");
        Location location2 = new Location("D1", "b1", "p1");
        assertEquals(location1, location2);
    }
}