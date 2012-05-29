package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationServiceTest {
    @Mock
    AllLocations allLocations;

    LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        locationService = new LocationService(allLocations);
    }

    @Test
    public void shouldValidateLocationRequests() {
        String panchayat = "panchayat";
        String block = "block";
        LocationRequest locationRequest = new LocationRequest(null, block, panchayat);

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        assertTrue(locationCreationResponse.getMessage().contains("Blank district, block or panchayat"));
    }

    @Test
    public void shouldAddTheLocationToDB() {
        String panchayat = "panchayat";
        String block = "block";
        String district = "district";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);

        locationService.add(locationRequest);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations).add(captor.capture());
        Location location = captor.getValue();
        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals("S01D001B001V001", location.getLocationId());
    }
}
