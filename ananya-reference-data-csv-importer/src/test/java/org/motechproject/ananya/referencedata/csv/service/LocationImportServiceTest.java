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
import org.motechproject.ananya.referencedata.flw.repository.AllLocationFilename;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadLocationMetaData;
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
    @Mock
    AllUploadLocationMetaData allUploadMetaData;
    
    @Mock
    AllLocationFilename allLocationFilename;

    @Before
    public void setUp() {
        locationImportService = new LocationImportService(allLocations, frontLineWorkerService, syncService,allUploadMetaData,allLocationFilename);
    }


    @Test
    public void shouldDoNothingIfAllLocationsInTheCsvRequestAreIdenticalToTheExistingLocationsInTheDb() {
        List<LocationImportCSVRequest> locationImportCSVRequests = new ArrayList<>();
        locationImportCSVRequests.add(locationImportCSVRequest("S2", "D2", "B2", "P2", LocationStatus.VALID.getDescription(), null,null,null, null));
        locationImportCSVRequests.add(locationImportCSVRequest("S3", "D3", "B3", "P3", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2", "S2"));
        locationImportCSVRequests.add(locationImportCSVRequest("S4", "D4", "B4", "P4", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2", "S2"));

        when(allLocations.getFor("S2", "D2", "B2", "P2")).thenReturn(new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null));
        when(allLocations.getFor("S3", "D3", "B3", "P3")).thenReturn(new Location("S3", "D3", "B3", "P3", LocationStatus.INVALID, new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null)));
        when(allLocations.getFor("S4", "D4", "B4", "P4")).thenReturn(new Location("S4", "D4", "B4", "P4", LocationStatus.INVALID, new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null)));

        locationImportService.addAllWithoutValidations(locationImportCSVRequests);
        verify(allLocations,never()).update(any(Location.class));
        verify(frontLineWorkerService,never()).updateWithAlternateLocationForFLWsWith(any(Location.class));
        verify(syncService,never()).syncLocation(any(Location.class));
    }

    @Test
    public void shouldBulkSaveLocation() {
        List<LocationImportCSVRequest> locationImportCSVRequests = new ArrayList<>();
        locationImportCSVRequests.add(locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.NEW.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("S2", "D2", "B2", "P2", LocationStatus.VALID.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("S3", "D3", "B3", "P3", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2", "S2"));
        locationImportCSVRequests.add(locationImportCSVRequest("S4", "D4", "B4", "P4", LocationStatus.IN_REVIEW.getDescription()));
        locationImportCSVRequests.add(locationImportCSVRequest("S5", "D5", "B5", "P5", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2", "S2"));

        Location validLocation = new Location("S2", "D2", "B2", "P2", LocationStatus.IN_REVIEW, null);
        when(allLocations.getFor("S2", "D2", "B2", "P2")).thenReturn(validLocation);
        when(allLocations.getFor("S3", "D3", "B3", "P3")).thenReturn(new Location("S3", "D3", "B3", "P3", LocationStatus.IN_REVIEW, null));
        when(allLocations.getFor("S4", "D4", "B4", "P4")).thenReturn(new Location("S4", "D4", "B4", "P4", LocationStatus.NOT_VERIFIED, null));
        Location identicalExistingLocationInDb = new Location("S5", "D5", "B5", "P5", LocationStatus.INVALID, validLocation);
        when(allLocations.getFor("S5", "D5", "B5", "P5")).thenReturn(identicalExistingLocationInDb);

        locationImportService.addAllWithoutValidations(locationImportCSVRequests);

        InOrder orderedExecution = inOrder(allLocations);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        orderedExecution.verify(allLocations, times(1)).add(locationArgumentCaptor.capture());
        Location location1 = locationArgumentCaptor.getValue();
        assertLocationDetails(location1, "D1", LocationStatus.VALID, "S1");

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());
        Location location2 = locationArgumentCaptor.getValue();
        assertLocationDetails(location2, "D2", LocationStatus.VALID, "S2");

        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());
        Location location3 = locationArgumentCaptor.getValue();
        assertLocationDetails(location3, "D4", LocationStatus.IN_REVIEW, "S4");

        orderedExecution.verify(allLocations).update(locationArgumentCaptor.capture());

        Location location4 = locationArgumentCaptor.getValue();
        assertLocationDetails(location4, "D3", LocationStatus.INVALID, "S3");

        assertEquals(location2.getDistrict(), location4.getAlternateLocation().getDistrict());

        locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(frontLineWorkerService).updateWithAlternateLocationForFLWsWith(locationArgumentCaptor.capture());
        Location location5 = locationArgumentCaptor.getValue();
        assertEquals(location4, location5);

        verifySync(location1, location2, location3, location4,identicalExistingLocationInDb);
    }

    private void assertLocationDetails(Location location, String expectedDistrictName, LocationStatus expectedStatus, String expectedState) {
        assertEquals(expectedDistrictName, location.getDistrict());
        assertEquals(expectedStatus, location.getStatus());
        assertEquals(expectedState, location.getState());
    }

    @Test
    public void shouldGetLocationForASpecificDistrictBlockAndPanchayat() {
        Location location = new Location("state", "District", "Block", "Panchayat", LocationStatus.VALID, null);
        when(allLocations.getFor("state", "district", "block", "panchayat")).thenReturn(location);

        Location actualLocation = locationImportService.getFor("state", "district", "block", "panchayat");

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

    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status, String newDistrict, String newBlock, String newPanchayat, String newState) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(state, district, block, panchayat, status, newState, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(state, district, block, panchayat, status, null, null, null, null);
    }
}