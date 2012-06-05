package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationServiceTest {
    @Mock
    AllLocations allLocations;

    @Captor
    ArgumentCaptor<List<Location>> captor;

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

        assertEquals("Blank district, block or panchayat", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateDuplicateLocationCreationRequests() {
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        when(allLocations.getAll()).thenReturn(new ArrayList<Location>(){
            {
                add(new Location("district", "block", "panchayat"));
            }
        });

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        assertEquals("Location already present", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldAddTheLocationToDB() {
        String panchayat = "panchayat";
        String block = "block";
        String district = "district";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations).add(captor.capture());
        Location location = captor.getValue();
        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals("S01D001B001V001", location.getLocationId());
        assertEquals("Location created successfully", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldGetAllLocations() {
        ArrayList<Location> expectedLocations = new ArrayList<Location>();
        when(allLocations.getAll()).thenReturn(expectedLocations);

        List<Location> actualLocations = locationService.getAll();

        assertEquals(expectedLocations, actualLocations);
    }

    @Test
    public void shouldBulkSaveLocation() {
        String panchayat = "panchayat";
        String block = "block";
        String district1 = "district1";
        String district2 = "district2";
        LocationRequest locationRequest1 = new LocationRequest(district1, block, panchayat);
        LocationRequest locationRequest2 = new LocationRequest(district2, block, panchayat);
        ArrayList<LocationRequest> locationRequests = new ArrayList<LocationRequest>();
        locationRequests.add(locationRequest1);
        locationRequests.add(locationRequest2);

        locationService.addAllWithoutValidations(locationRequests);

        verify(allLocations).addAll(captor.capture());
        List<Location> value = captor.getValue();
        assertEquals(2, value.size());
        assertEquals(district1, value.get(0).getDistrict());
        assertEquals(district2, value.get(1).getDistrict());
    }
}
