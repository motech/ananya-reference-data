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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceTest {
    @Mock
    private AllLocations allLocations;
    private LocationService locationService;

    @Before
    public void setUp() {
        locationService = new LocationService(allLocations);
    }

    @Test
    public void shouldGetAnExistingLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        LocationRequest request = new LocationRequest(district, block, panchayat, "VERIFIED");
        Location expectedLocation = new Location();
        when(allLocations.getFor(district, block, panchayat)).thenReturn(expectedLocation);

        Location actualLocation = locationService.getLocation(request);

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void shouldGetAllValidLocation() {
        ArrayList<Location> expectedLocationList = new ArrayList<>();
        when(allLocations.getAllForStatus(LocationStatus.VERIFIED)).thenReturn(expectedLocationList);

        List<Location> actualLocationList = locationService.getAllValidLocations();

        assertEquals(expectedLocationList, actualLocationList);
    }
}
