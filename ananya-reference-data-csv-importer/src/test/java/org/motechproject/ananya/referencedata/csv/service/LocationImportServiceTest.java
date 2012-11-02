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
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
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
        locationImportService = new LocationImportService(allLocations, frontLineWorkerService);
    }

    @Test
    public void shouldBulkSaveLocation() {
        List<LocationImportRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(new LocationImportRequest("d1", "b1", "p1", "new"));
        locationImportRequests.add(new LocationImportRequest("d2", "b2", "p2", "valid"));
        locationImportRequests.add(new LocationImportRequest("d3", "b3", "p3", "invalid", "d2", "b2", "p2"));

        when(allLocations.getFor("d2", "b2", "p2")).thenReturn(new Location("d2", "b2", "p2", "in_review", null));
        when(allLocations.getFor("d3", "b3", "p3")).thenReturn(new Location("d3", "b3", "p3", "in_review", null));

        locationImportService.addAllWithoutValidations(locationImportRequests);

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations, times(1)).add(locationArgumentCaptor.capture());
        Location location1 = locationArgumentCaptor.getValue();
        assertEquals("d1", location1.getDistrict());
        assertEquals("VALID", location1.getStatus());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations, times(2)).update(locationArgumentCaptor.capture());
        List<Location> locations = locationArgumentCaptor.getAllValues();
        Location location2 = locations.get(0);
        assertEquals("d2", location2.getDistrict());
        assertEquals("VALID", location2.getStatus());
        Location location3 = locations.get(1);
        assertEquals("d3", location3.getDistrict());
        assertEquals("INVALID", location3.getStatus());
        assertEquals(location2.getDistrict(), location3.getAlternateLocation().getDistrict());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(frontLineWorkerService).updateWithAlternateLocationForFLWsWith(locationArgumentCaptor.capture());
        Location location4 = locationArgumentCaptor.getValue();
        assertEquals(location3, location4);
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("district", "block", "panchayat", "VALID", null);
        when(allLocations.getFor("district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationImportService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }
}