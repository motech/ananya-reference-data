package org.motechproject.ananya.referencedata.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocationListTest {

    private LocationList locationList;

    @Before
    public void setUp() {
        ArrayList<Location> allLocations = new ArrayList<Location>();
        allLocations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        allLocations.add(new Location("D1", "B1", "P2", 1, 1, 2));
        allLocations.add(new Location("D1", "B2", "P1", 1, 2, 1));
        allLocations.add(new Location("D2", "B1", "P1", 2, 1, 1));
        locationList = new LocationList(allLocations);
    }

    @Test
    public void shouldUpdateTheLocationWithNewDistrictCodeIfDistrictNotAlreadyPresent() {
        Location location = new Location("D3", "B3", "P3");

        Location updatedLocation = locationList.updateLocationCode(location);

        assertEquals(3, (int) updatedLocation.getDistrictCode());
        assertEquals(1, (int) updatedLocation.getBlockCode());
        assertEquals(1, (int) updatedLocation.getPanchayatCode());
    }

    @Test
    public void shouldUpdateTheLocationWithNewBlockCodeIfBlockNotAlreadyPresent() {
        Location location = new Location("D2", "B2", "P1");

        Location updatedLocation = locationList.updateLocationCode(location);

        assertEquals(2, (int) updatedLocation.getDistrictCode());
        assertEquals(2, (int) updatedLocation.getBlockCode());
        assertEquals(1, (int) updatedLocation.getPanchayatCode());
    }

    @Test
    public void shouldUpdateTheLocationWithNewPanchayatCodeIfPanchayatNotAlreadyPresent() {
        Location location = new Location("D2", "B2", "P3");

        Location updatedLocation = locationList.updateLocationCode(location);

        assertEquals(2, (int) updatedLocation.getDistrictCode());
        assertEquals(2, (int) updatedLocation.getBlockCode());
        assertEquals(1, (int) updatedLocation.getPanchayatCode());
    }

    @Test
    public void shouldNotUpdateTheCodesIfAlreadyPresent() {
        Location location = new Location("D2", "B1", "P1");

        Location updatedLocation = locationList.updateLocationCode(location);

        assertEquals(2, (int) updatedLocation.getDistrictCode());
        assertEquals(1, (int) updatedLocation.getBlockCode());
        assertEquals(1, (int) updatedLocation.getPanchayatCode());
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
}
