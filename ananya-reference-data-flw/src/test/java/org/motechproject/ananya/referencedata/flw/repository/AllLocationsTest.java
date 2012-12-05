package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void shouldReturnLocationWhenLocationForGivenLocationDetailsExists() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location(district, block, panchayat, LocationStatus.NOT_VERIFIED, null);

        allLocations.add(location);

        Location locationFromDB = allLocations.getFor(district, block, panchayat);
        assertNotNull(locationFromDB);
    }

    @Test
    public void shouldReturnLocationWhenLocationForGivenLocationDetailsExistsIgnoringCase() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location(district, block, panchayat, LocationStatus.NOT_VERIFIED, null);

        allLocations.add(location);

        Location locationFromDB = allLocations.getFor("DistRict", "BLOCK", "panchayat");
        assertNotNull(locationFromDB);
    }

    @Test
    public void shouldLoadLocationsBasedOnStatus() {
        Location validLocation1 = new Location("district", "block", "panchayat", LocationStatus.VALID, null);
        Location validLocation2 = new Location("district3", "block3", "panchayat3", LocationStatus.VALID, null);
        Location unverifiedLocation = new Location("district1", "block1", "panchayat1", LocationStatus.NOT_VERIFIED, null);
        Location invalidLocation = new Location("district2", "block2", "panchayat2", LocationStatus.INVALID, null);

        template.saveOrUpdateAll(Arrays.asList(validLocation1, unverifiedLocation, validLocation2, invalidLocation));

        List<Location> locationsList = allLocations.getForStatuses(LocationStatus.VALID, LocationStatus.NOT_VERIFIED);

        assertEquals(3, locationsList.size());
        assertTrue(locationsList.contains(validLocation1));
        assertTrue(locationsList.contains(validLocation2));
        assertTrue(locationsList.contains(unverifiedLocation));
    }

    @Test
    public void shouldReturnNullWhenALocationDoesNotExistInDB() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";

        Location locationInDB = allLocations.getFor(district, block, panchayat);
        assertNull(locationInDB);
    }

    @Test
    public void shouldUpdateGivenLocation_AndAllLocationsWhichHasGivenInvalidLocationAsAlternateLocation() {
        final Location givenLocation = new Location("district", "block", "panchyat", LocationStatus.IN_REVIEW, null);
        List<Location> existingInvalidLocations = new ArrayList<Location>(){{
            add(new Location("d1", "b1", "p1", LocationStatus.INVALID, givenLocation));
            add(new Location("d2", "b2", "p2", LocationStatus.INVALID, givenLocation));
        }};
       template.save(givenLocation);
       template.saveOrUpdateAll(existingInvalidLocations);
       Location alternateLocation = new Location("district2", "block2", "panchayat2", LocationStatus.VALID, null);
       givenLocation.setStatus(LocationStatus.INVALID);
       givenLocation.setAlternateLocation(alternateLocation);
       template.save(alternateLocation);
       template.update(givenLocation);

       allLocations.update(givenLocation);

       Location updatedLocation = allLocations.getFor(givenLocation.getDistrict(), givenLocation.getBlock(), givenLocation.getPanchayat());
       assertEquals(givenLocation, updatedLocation);
       Location locationWithNewAlternateLocation1 = allLocations.getFor(existingInvalidLocations.get(0).getDistrict(), existingInvalidLocations.get(0).getBlock(), existingInvalidLocations.get(0).getPanchayat());
       Location locationWithNewAlternateLocation2 = allLocations.getFor(existingInvalidLocations.get(1).getDistrict(), existingInvalidLocations.get(1).getBlock(), existingInvalidLocations.get(1).getPanchayat());
       assertEquals(alternateLocation, locationWithNewAlternateLocation1.getAlternateLocation());
       assertEquals(alternateLocation, locationWithNewAlternateLocation2.getAlternateLocation());
    }

    @Test
    public void shouldUpdateGivenLocationAndShouldNotUpdateOthers_IfGivenLocationIsNotInvalid() {
        Location givenLocation = new Location("district", "block", "panchayat", LocationStatus.IN_REVIEW, null);
        Location existingLocation = new Location("d", "b", "p", LocationStatus.INVALID, givenLocation);
        template.save(givenLocation);
        template.save(existingLocation);
        givenLocation.setStatus(LocationStatus.VALID);

        allLocations.update(givenLocation);

        Location updatedLocation = allLocations.getFor(givenLocation.getDistrict(), givenLocation.getBlock(), givenLocation.getPanchayat());
        assertEquals(givenLocation, updatedLocation);
        Location existingLocationNotUpdated = allLocations.getFor(existingLocation.getDistrict(), existingLocation.getBlock(), existingLocation.getPanchayat());
        assertEquals(existingLocation, existingLocationNotUpdated);
    }
}