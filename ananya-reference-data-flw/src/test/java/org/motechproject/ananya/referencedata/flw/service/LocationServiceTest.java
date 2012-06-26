package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.validators.LocationValidator;

import java.util.ArrayList;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationServiceTest {
    @Mock
    AllLocations allLocations;
    @Mock
    private LocationValidator locationValidator;
    @Captor
    ArgumentCaptor<Set<Location>> captor;

    LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        locationService = new LocationService(allLocations, locationValidator);
    }

    @Test
    public void shouldValidateLocationRequests() {
        String panchayat = "panchayat";
        String block = "block";
        LocationRequest locationRequest = new LocationRequest(null, block, panchayat);
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        flwValidationResponse.forBlankFieldsInLocation();
        when(locationValidator.validate(new Location(null, "block", "panchayat"))).thenReturn(flwValidationResponse);

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        assertEquals("Blank district, block or panchayat", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateDuplicateLocationCreationRequests() {
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        flwValidationResponse.forDuplicateLocation();
        when(locationValidator.validate(new Location("district", "block", "panchayat"))).thenReturn(flwValidationResponse);

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        assertEquals("Location already present", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldAddTheLocationToDB() {
        String panchayat = "panchayat";
        String block = "block";
        String district = "district";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);
        when(locationValidator.validate(new Location("district", "block", "panchayat"))).thenReturn(new FLWValidationResponse());

        LocationCreationResponse locationCreationResponse = locationService.add(locationRequest);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations).add(captor.capture());
        Location location = captor.getValue();
        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals("Location created successfully", locationCreationResponse.getMessage());
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
        Set<Location> value = captor.getValue();
        assertEquals(2, value.size());
        assertTrue(value.contains(new Location(district1, block, panchayat)));
        assertTrue(value.contains(new Location(district2, block, panchayat)));
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("district", "block", "panchayat");
        when(allLocations.getFor("district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }
}
