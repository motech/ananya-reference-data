package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.utils.LocationImportCSVRequestBuilder;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
    public void shouldDoNothingIfAllLocationsInTheCsvRequestAreIdenticalToTheExistingLocationsInTheDb() {
        List<LocationImportCSVRequest> locationImportCSVRequests = new ArrayList<>();
        locationImportCSVRequests.add(locationImportCSVRequest("d2", "b2", "p2", LocationStatus.VALID.getDescription(), null,null,null));
        locationImportCSVRequests.add(locationImportCSVRequest("d3", "b3", "p3", LocationStatus.INVALID.getDescription(), "d2", "b2", "p2"));
        locationImportCSVRequests.add(locationImportCSVRequest("d4", "b4", "p4", LocationStatus.INVALID.getDescription(), "d2", "b2", "p2"));

        when(allLocations.getFor("d2", "b2", "p2")).thenReturn(new Location("d2", "b2", "p2", LocationStatus.VALID, null));
        when(allLocations.getFor("d3", "b3", "p3")).thenReturn(new Location("d3", "b3", "p3", LocationStatus.INVALID, new Location("d2", "b2", "p2", LocationStatus.VALID, null)));
        when(allLocations.getFor("d4", "b4", "p4")).thenReturn(new Location("d4", "b4", "p4", LocationStatus.INVALID, new Location("d2", "b2", "p2", LocationStatus.VALID, null)));

        locationImportService.addAllWithoutValidations(locationImportCSVRequests);
        verify(allLocations,never()).update(any(Location.class));
        verify(frontLineWorkerService,never()).updateWithAlternateLocationForFLWsWith(any(Location.class));
        verify(syncService,never()).syncLocation(any(Location.class));
    }

    @Test
    public void shouldBulkSaveLocation() {
        List<LocationImportCSVRequest> locationImportCSVRequests = new ArrayList<>();
        locationImportCSVRequests.add(locationImportCSVRequest("d1", "b1", "p1", LocationStatus.NEW.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("d2", "b2", "p2", LocationStatus.VALID.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("d3", "b3", "p3", LocationStatus.INVALID.getDescription(), "d2", "b2", "p2"));
        locationImportCSVRequests.add(locationImportCSVRequest("d4", "b4", "p4", LocationStatus.IN_REVIEW.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("d5", "b5", "p5", LocationStatus.INVALID.getDescription(), "d2", "b2", "p2"));

        Location validLocation = new Location("d2", "b2", "p2", LocationStatus.IN_REVIEW, null);
        when(allLocations.getFor("d2", "b2", "p2")).thenReturn(validLocation);
        when(allLocations.getFor("d3", "b3", "p3")).thenReturn(new Location("d3", "b3", "p3", LocationStatus.IN_REVIEW, null));
        when(allLocations.getFor("d4", "b4", "p4")).thenReturn(new Location("d4", "b4", "p4", LocationStatus.NOT_VERIFIED, null));
        Location identicalExistingLocationInDb = new Location("d5", "b5", "p5", LocationStatus.INVALID, validLocation);
        when(allLocations.getFor("d5", "b5", "p5")).thenReturn(identicalExistingLocationInDb);

        locationImportService.addAllWithoutValidations(locationImportCSVRequests);

        InOrder orderedExecution = inOrder(allLocations);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        orderedExecution.verify(allLocations, times(1)).add(locationArgumentCaptor.capture());
        Location location1 = locationArgumentCaptor.getValue();
        assertLocationDetails(location1, "d1", LocationStatus.VALID);

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());
        Location location2 = locationArgumentCaptor.getValue();
        assertLocationDetails(location2, "d2", LocationStatus.VALID);

        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());
        Location location3 = locationArgumentCaptor.getValue();
        assertLocationDetails(location3, "d4", LocationStatus.IN_REVIEW);

        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());

        Location location4 = locationArgumentCaptor.getValue();
        assertLocationDetails(location4, "d3", LocationStatus.INVALID);

        assertEquals(location2.getDistrict(), location4.getAlternateLocation().getDistrict());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(frontLineWorkerService).updateWithAlternateLocationForFLWsWith(locationArgumentCaptor.capture());
        Location location5 = locationArgumentCaptor.getValue();
        assertEquals(location4, location5);

        verifySync(location1, location2, location3, location4,identicalExistingLocationInDb);
    }

    private void assertLocationDetails(Location location, String expectedDistrictName, LocationStatus expectedStatus) {
        assertEquals(expectedDistrictName, location.getDistrict());
        assertEquals(expectedStatus, location.getStatus());
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("district", "block", "panchayat", LocationStatus.VALID, null);
        when(allLocations.getFor("district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationImportService.getFor("district", "block", "panchayat");

        assertEquals(location, actualLocation);
    }

    private void verifySync(Location location1, Location location2, Location location3, Location location4, Location identicalExistingLocationInDb) {
        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(syncService, times(4)).syncLocation(locationCaptor.capture());
        List<Location> locationList = locationCaptor.getAllValues();
        locationList.contains(location1);
        locationList.contains(location2);
        locationList.contains(location3);
        locationList.contains(location4);
        assertFalse(locationList.contains(identicalExistingLocationInDb));
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(district, block, panchayat, status, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(district, block, panchayat, status, null, null, null);
    }
}