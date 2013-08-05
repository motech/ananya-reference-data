package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {
    @Mock
    private AllLocations allLocations;
    @Mock
    private SyncService syncService;
    private LocationService locationService;

    @Before
    public void setUp() {
        locationService = new LocationService(allLocations, syncService);
    }

    @Test
    public void shouldGetAnExistingLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String state = "state";
        LocationRequest request = new LocationRequest(state, district, block, panchayat, "VALID");
        Location expectedLocation = new Location();
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(expectedLocation);

        Location actualLocation = locationService.createAndFetch(request);

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void shouldMapToAlternateLocationIfLocationToMapIsInvalid(){
        String district = "d1";
        String block = "b1";
        String panchayat = "p1";
        String state = "state";
        Location alternateLocation = new Location(state, "d2", "b2", "p2", LocationStatus.VALID, null);
        Location locationToMap = new Location(state, district, block, panchayat, LocationStatus.INVALID, alternateLocation);
        LocationRequest request = new LocationRequest(state, district, block, panchayat);
        when(allLocations.getFor(state, district,block,panchayat)).thenReturn(locationToMap);

        Location locationToBeMapped = locationService.createAndFetch(request);

        assertEquals(alternateLocation,locationToBeMapped);
    }

    @Test
    public void shouldCreateANewLocationIfLocationDoesNotExist() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String state = "state";
        LocationRequest request = new LocationRequest(state, district, block, panchayat, "VALID");
        Location expectedLocation = new Location(state, district, block, panchayat, LocationStatus.NOT_VERIFIED, null);
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(null);

        Location actualLocation = locationService.createAndFetch(request);

        verify(allLocations).add(expectedLocation);
        verify(syncService).syncLocation(expectedLocation);
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void shouldGetAllValidLocation() {
        ArrayList<Location> expectedLocationList = new ArrayList<>();
        when(allLocations.getForStatuses(LocationStatus.VALID)).thenReturn(expectedLocationList);

        List<Location> actualLocationList = locationService.getAllValidLocations();

        assertEquals(expectedLocationList, actualLocationList);
    }

    @Test
    public void shouldGetLocationsToBeVerified() {
        List<Location> expectedLocationList = Arrays.asList(new Location("state", "d1", "b1", "p1", LocationStatus.NOT_VERIFIED, null));
        when(allLocations.getForStatuses(LocationStatus.NOT_VERIFIED, LocationStatus.IN_REVIEW)).thenReturn(expectedLocationList);

        List<Location> actualLocationList = locationService.getLocationsToBeVerified();

        assertEquals(expectedLocationList, actualLocationList);
    }
}
