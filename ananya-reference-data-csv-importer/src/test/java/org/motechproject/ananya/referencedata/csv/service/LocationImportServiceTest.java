package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import java.util.ArrayList;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationImportServiceTest {
    @Mock
    AllLocations allLocations;
    @Mock
    private LocationImportValidator locationValidator;
    @Captor
    ArgumentCaptor<Set<Location>> captor;

    LocationImportService locationImportService;

    @Before
    public void setUp() {
        initMocks(this);
        locationImportService = new LocationImportService(allLocations, locationValidator);
    }

    @Test
    public void shouldValidateLocationRequests() {
        String panchayat = "panchayat";
        String block = "block";
        LocationRequest locationRequest = new LocationRequest(null, block, panchayat);
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forBlankFieldsInLocation();
        when(locationValidator.validate(new Location(null, "block", "panchayat"))).thenReturn(locationValidationResponse);

        LocationCreationResponse locationCreationResponse = locationImportService.add(locationRequest);

        assertEquals("Blank district, block or panchayat", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateDuplicateLocationCreationRequests() {
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forDuplicateLocation();
        when(locationValidator.validate(new Location("district", "block", "panchayat"))).thenReturn(locationValidationResponse);

        LocationCreationResponse locationCreationResponse = locationImportService.add(locationRequest);

        assertEquals("Location already present", locationCreationResponse.getMessage());
    }

    @Test
    public void shouldAddTheLocationToDB() {
        String panchayat = "panchayat";
        String block = "block";
        String district = "district";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);
        when(locationValidator.validate(new Location("district", "block", "panchayat"))).thenReturn(new LocationValidationResponse());

        LocationCreationResponse locationCreationResponse = locationImportService.add(locationRequest);

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

        locationImportService.addAllWithoutValidations(locationRequests);

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

        Location actualLocation = locationImportService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }
}
