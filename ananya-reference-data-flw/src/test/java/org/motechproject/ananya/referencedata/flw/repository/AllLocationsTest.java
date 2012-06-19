package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class AllLocationsTest extends SpringIntegrationTest {

    @Autowired
    AllLocations allLocations;

    @Before
    @After
    public void setUp() {
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldAddLocationToDB() {
        Location location = new Location("district", "block", "panchayat");

        allLocations.add(location);

        List<Location> locations = template.loadAll(Location.class);
        assertEquals(1, locations.size());
        assertTrue(location.equals(locations.get(0)));
        assertNotNull(locations.get(0).getLastModified());
    }

    @Test
    public void shouldReturnAllLocationsFromDB() {
        Location location1 = new Location("district", "block", "panchayat");
        Location location2 = new Location("district", "block", "panchayat");

        allLocations.add(location1);
        allLocations.add(location2);

        List<Location> locations = allLocations.getAll();

        assertEquals(2, locations.size());
    }

    @Test
    public void shouldReturnLocationWhenLocationForGivenLocationDetailsExists() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location(district, block, panchayat);

        allLocations.add(location);

        Location locationFromDB = allLocations.getFor(district, block, panchayat);
        assertNotNull(locationFromDB);
    }

    @Test
    public void shouldReturnLocationWhenLocationForGivenLocationDetailsExistsIgnoringCase() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location(district, block, panchayat);

        allLocations.add(location);

        Location locationFromDB = allLocations.getFor("DistRict", "BLOCK", "panchayat");
        assertNotNull(locationFromDB);
    }

    @Test
    public void shouldAddMultipleLocations() {
        String district = "district";
        String district1 = "district1";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location(district, block, panchayat);
        Location location1 = new Location(district1, block, panchayat);
        List<Location> locations = new ArrayList<Location>();
        locations.add(location);
        locations.add(location1);

        allLocations.addAll(locations);

        List<Location> locationsFromDb = allLocations.getAll();
        assertEquals(2, locationsFromDb.size());
    }

    @Test
    public void shouldReturnNullWhenALocationDoesNotExistInDB(){
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";

        Location locationInDB = allLocations.getFor(district, block, panchayat);

        assertNull(locationInDB);
    }
}