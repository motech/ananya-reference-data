package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocationListTest {

    private LocationList locationList;

    @Before
    public void setUp() {
        ArrayList<Location> allLocations = new ArrayList<Location>();
        allLocations.add(new Location("D1", "B1", "P1"));
        allLocations.add(new Location("D1", "B1", "P2"));
        allLocations.add(new Location("D1", "B2", "P1"));
        allLocations.add(new Location("D2", "B1", "P1"));
        locationList = new LocationList(allLocations);
    }

    @Test
    public void shouldReturnIfLocationIsAlreadyPresentInTheDb() {
        Location alreadyPresentLocation = new Location("D1","B1","P1");
        Location locationNotPresent = new Location("D5","B1","P1");

        boolean isPresent = locationList.isAlreadyPresent(alreadyPresentLocation);
        assertTrue(isPresent);

        isPresent = locationList.isAlreadyPresent(locationNotPresent);
        assertFalse(isPresent);
    }

    @Test
    public void shouldReturnTheLocationForAParticularDistrictBlockAndPanchayat() {
        Location actualLocation = locationList.findFor("D1", "B1", "P1");
        assertEquals(new Location("D1","B1","P1"),actualLocation);
    }

    @Test
    public void shouldReturnNullLocationIfTheDistrictBlockAndPanchayatDoNotExist() {
        Location actualLocation = locationList.findFor("aragorn", "arwen", "sam");
        assertNull(actualLocation);
    }
}
