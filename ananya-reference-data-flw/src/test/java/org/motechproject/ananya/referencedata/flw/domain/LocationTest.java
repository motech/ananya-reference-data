package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationTest {

    @Test
    public void shouldIgnoreCaseWhileComparingLocations() {
        Location location1 = new Location("state", "d1", "B1", "p1", LocationStatus.NOT_VERIFIED, null);
        Location location2 = new Location("state", "D1", "b1", "p1", LocationStatus.NOT_VERIFIED, null);
        assertEquals(location1, location2);
    }

    @Test
    public void shouldCheckIfLocationStatusIsValid() {
        Location location1 = new Location("state", "D1", "b1", "p1", LocationStatus.VALID, null);
        Location location2 = new Location("state", "d1", "B1", "p1", LocationStatus.INVALID, null);
        Location location3 = new Location("state", "d1", "B1", "p1", LocationStatus.IN_REVIEW, null);
        Location location4 = new Location("state", "d1", "B1", "p1", LocationStatus.NOT_VERIFIED, null);

        assertTrue(location1.isValidatedLocation());
        assertFalse(location2.isValidatedLocation());
        assertFalse(location3.isValidatedLocation());
        assertFalse(location4.isValidatedLocation());
    }

    @Test
    public void shouldCheckIfLocationIsInvalid() {
        Location location1 = new Location("state", "D1", "b1", "p1", LocationStatus.VALID, null);
        Location location2 = new Location("state", "d1", "B1", "p1", LocationStatus.INVALID, null);
        Location location3 = new Location("state", "d1", "B1", "p1", LocationStatus.IN_REVIEW, null);
        Location location4 = new Location("state", "d1", "B1", "p1", LocationStatus.NOT_VERIFIED, null);

        assertFalse(location1.isInvalid());
        assertTrue(location2.isInvalid());
        assertFalse(location3.isInvalid());
        assertFalse(location4.isInvalid());
    }

    @Test
    public void shouldCapitalizeLocationDetails() {
        Location location = new Location("state", "d1", "b1", "p1", LocationStatus.VALID, null);
        assertEquals("D1", location.getDistrict());
        assertEquals("B1", location.getBlock());
        assertEquals("P1", location.getPanchayat());
        assertEquals("State", location.getState());
    }
}
