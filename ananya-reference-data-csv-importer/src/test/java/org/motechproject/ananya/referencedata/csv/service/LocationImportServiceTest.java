package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;

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
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Captor
    ArgumentCaptor<Set<Location>> captor;
    LocationImportService locationImportService;

    @Before
    public void setUp() {
        initMocks(this);
        locationImportService = new LocationImportService(allLocations, locationValidator, frontLineWorkerService);
    }

    @Test
    public void shouldBulkSaveLocation() {
        String panchayat = "panchayat";
        String block = "block";
        String district1 = "district1";
        String district2 = "district2";
        Location location = new Location(district2, block, panchayat, LocationStatus.VALID.name(), null);
        LocationImportRequest locationRequest1 = new LocationImportRequest(district1, block, panchayat, "VALID");
        LocationImportRequest locationRequest2 = new LocationImportRequest(district2, block, panchayat, "INVALID");
        ArrayList<LocationImportRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationRequest1);
        locationImportRequests.add(locationRequest2);
        when(allLocations.getFor(district1, block, panchayat)).thenReturn(null);
        when(allLocations.getFor(district2, block, panchayat)).thenReturn(location);

        locationImportService.addAllWithoutValidations(locationImportRequests);

        verify(allLocations).addAll(captor.capture());
        Set<Location> value = captor.getValue();
        assertEquals(2, value.size());
        assertTrue(value.contains(new Location(district1, block, panchayat, "VALID", null)));
        assertTrue(value.contains(new Location(district2, block, panchayat, "INVALID", null)));
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("district", "block", "panchayat", "VALID", null);
        when(allLocations.getFor("district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationImportService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }

    @Test
    public void shouldAddLocationsWithAlternateLocationsAndUpfateFLWsForBulkAddition() {
        String panchayat = "panchayat";
        String block = "block";
        String district1 = "district1";
        String district2 = "district2";
        LocationImportRequest locationRequest2 = new LocationImportRequest(
                district2, block, panchayat, "INVALID", district1, block, panchayat);
        ArrayList<LocationImportRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationRequest2);
        Location location = new Location(district2, block, panchayat, LocationStatus.NOT_VERIFIED.name(), null);
        Location alternateLocation = new Location(district1, block, panchayat, LocationStatus.VALID.name(), null);
        when(allLocations.getFor(district2, block, panchayat)).thenReturn(location);
        when(allLocations.getFor(district1, block, panchayat)).thenReturn(alternateLocation);

        locationImportService.addAllWithoutValidations(locationImportRequests);

        verify(allLocations).addAll(captor.capture());
        Set<Location> locationsSaved = captor.getValue();
        location.setAlternateLocation(alternateLocation);
        location.setStatus(LocationStatus.INVALID);
        assertTrue(locationsSaved.contains(location));

        verify(frontLineWorkerService).updateWithAlternateLocationForFLWsWith(location);
    }
}