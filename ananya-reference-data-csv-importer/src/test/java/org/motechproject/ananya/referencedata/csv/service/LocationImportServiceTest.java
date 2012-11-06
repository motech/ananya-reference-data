package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocationImportServiceTest {
    @Mock
    AllLocations allLocations;
    @Mock
    private LocationImportValidator locationValidator;
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private SyncService syncService;
    LocationImportService locationImportService;

    @Before
    public void setUp() {
        locationImportService = new LocationImportService(allLocations, frontLineWorkerService, syncService);
    }

    @Test
    public void shouldBulkSaveLocation() {
        List<LocationImportRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(new LocationImportRequest("d1", "b1", "p1", LocationStatus.NEW.toString()));
        locationImportRequests.add(new LocationImportRequest("d2", "b2", "p2", LocationStatus.VALID.toString()));
        locationImportRequests.add(new LocationImportRequest("d3", "b3", "p3", LocationStatus.INVALID.toString(), "d2", "b2", "p2"));
        locationImportRequests.add(new LocationImportRequest("d4", "b4", "p4", LocationStatus.IN_REVIEW.toString()));

        when(allLocations.getFor("d2", "b2", "p2")).thenReturn(new Location("d2", "b2", "p2", LocationStatus.IN_REVIEW, null));
        when(allLocations.getFor("d3", "b3", "p3")).thenReturn(new Location("d3", "b3", "p3", LocationStatus.IN_REVIEW, null));
        when(allLocations.getFor("d4", "b4", "p4")).thenReturn(new Location("d4", "b4", "p4", LocationStatus.NOT_VERIFIED, null));

        locationImportService.addAllWithoutValidations(locationImportRequests);

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations, times(1)).add(locationArgumentCaptor.capture());
        Location location1 = locationArgumentCaptor.getValue();
        assertEquals("d1", location1.getDistrict());
        assertEquals(LocationStatus.VALID, location1.getStatus());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations, times(3)).update(locationArgumentCaptor.capture());
        List<Location> locations = locationArgumentCaptor.getAllValues();
        Location location2 = locations.get(0);
        assertEquals("d2", location2.getDistrict());
        assertEquals(LocationStatus.VALID, location2.getStatus());
        Location location3 = locations.get(1);
        assertEquals("d4", location3.getDistrict());
        assertEquals(LocationStatus.IN_REVIEW, location3.getStatus());
        Location location4 = locations.get(2);
        assertEquals("d3", location4.getDistrict());
        assertEquals(LocationStatus.INVALID, location4.getStatus());
        assertEquals(location2.getDistrict(), location4.getAlternateLocation().getDistrict());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(frontLineWorkerService).updateWithAlternateLocationForFLWsWith(locationArgumentCaptor.capture());
        Location location5 = locationArgumentCaptor.getValue();
        assertEquals(location4, location5);

        verifySync(location1, location2, location3, location4);
    }

    private void verifySync(Location location1, Location location2, Location location3, Location location4) {
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(syncService, times(4)).syncLocation(locationCaptor.capture());
        List<Location> locationList = locationCaptor.getAllValues();
        locationList.contains(location1);
        locationList.contains(location2);
        locationList.contains(location3);
        locationList.contains(location4);
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("district", "block", "panchayat", LocationStatus.VALID, null);
        when(allLocations.getFor("district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationImportService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }
}