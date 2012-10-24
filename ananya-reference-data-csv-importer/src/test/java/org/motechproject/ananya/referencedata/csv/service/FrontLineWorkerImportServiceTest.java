package org.motechproject.ananya.referencedata.csv.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.response.FrontLineWorkerImportResponse;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FrontLineWorkerImportServiceTest {

    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private SyncService syncService;
    @Mock
    private Properties clientServicesProperties;


    @Captor
    ArgumentCaptor<List<FrontLineWorker>> captor;

    @Captor
    ArgumentCaptor<List<FrontLineWorker>> anotherCaptor;

    private FrontLineWorkerImportService frontLineWorkerImportService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImportService = new FrontLineWorkerImportService(allLocations, allFrontLineWorkers, syncService);
    }

    @Test
    public void shouldInvokeSyncServiceToPushFlwChangesWhenFlwIsUpdatedAndThereAreNoDuplicateMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, "VALID"));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat", "VALID"));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, "VALID"));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(Arrays.asList(frontLineWorker));

        frontLineWorkerImportService.createOrUpdate(frontLineWorkerImportRequest);

        verifySync(frontLineWorkerImportRequest);
    }

    @Test
    public void shouldInvokeSyncServiceToPushFlwChangesWhenFlwIsAddedAndThereAreNoDuplicateMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, "VALID"));

        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, "VALID"));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(Collections.<FrontLineWorker>emptyList());

        frontLineWorkerImportService.createOrUpdate(frontLineWorkerImportRequest);

        verifySync(frontLineWorkerImportRequest);
    }

    @Test
    public void shouldNotInvokeSyncServiceToPushFlwChangesWhenThereIsMoreThanONeFlwWithTheSameMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, "VALID"));

        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, "VALID"));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(Arrays.asList(new FrontLineWorker(),new FrontLineWorker()));

        frontLineWorkerImportService.createOrUpdate(frontLineWorkerImportRequest);
        verify(allFrontLineWorkers).createOrUpdate(any(FrontLineWorker.class));
        verify(syncService,never()).syncFrontLineWorker(any(FrontLineWorker.class));
    }

    @Test
    public void shouldAddFLWsInBulk() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454522";
        String msisdn2 = "911234454623";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest1 = new FrontLineWorkerImportRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat, "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerWebRequest2 = new FrontLineWorkerImportRequest(msisdn2, "name", "ASHA", new LocationRequest(district, block, panchayat, "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "VALID"));

        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        frontLineWorkerWebRequests.add(frontLineWorkerWebRequest1);
        frontLineWorkerWebRequests.add(frontLineWorkerWebRequest2);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerWebRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> frontLineWorkers = captor.getValue();
        assertEquals(msisdn1, frontLineWorkers.get(0).getMsisdn().toString());
        assertEquals(msisdn2, frontLineWorkers.get(1).getMsisdn().toString());
    }

    @Test
    public void shouldUpdateFLWInBulkIfOnlyOneFLWIsPresentInDBAndOnlyOneFLWIsPresentInTheCSVWithThatMSISDN() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454545";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest1 = new FrontLineWorkerImportRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat, "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "VALID"));
        FrontLineWorker expectedFLW = new FrontLineWorker();
        List<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        frontLineWorkers.add(expectedFLW);
        when(allFrontLineWorkers.getByMsisdn(Long.parseLong(msisdn1))).thenReturn(frontLineWorkers);
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        frontLineWorkerWebRequests.add(frontLineWorkerWebRequest1);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerWebRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertTrue(actualFLWs.contains(expectedFLW));
        assertEquals(1, actualFLWs.size());
        verify(syncService).syncAllFrontLineWorkers(anotherCaptor.capture());
        List<FrontLineWorker> actualFLWsForSync = anotherCaptor.getValue();
        assertEquals(actualFLWsForSync, actualFLWs);
    }

    @Test
    public void shouldAddRecordsAndNotUpdateIfThereAreDuplicatesWithinTheRequestList() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn = "12344545";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest1 = new FrontLineWorkerImportRequest(msisdn, "name", "ASHA", new LocationRequest(district, block, panchayat, "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerWebRequest2 = new FrontLineWorkerImportRequest(msisdn, "name", "ASHA", new LocationRequest(district, block, panchayat, "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "VALID"));
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        frontLineWorkerWebRequests.add(frontLineWorkerWebRequest1);
        frontLineWorkerWebRequests.add(frontLineWorkerWebRequest2);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerWebRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertEquals(2, actualFLWs.size());
    }

    @Test
    public void shouldUpdateExistingFLWBasedOnMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "asHA ";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        String status = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, status));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat", "NOT VERIFIED"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<>();
        frontLineWorkerList.add(frontLineWorker);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, "VALID"));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(frontLineWorkerList);

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());

        FrontLineWorker value = captor.getValue();

        assertEquals((Long) Long.parseLong(prefixedMsisdn), value.getMsisdn());
        assertEquals(newName, value.getName());
        assertEquals(Designation.ASHA.name(), value.getDesignation());
        assertEquals(newDistrict, value.getLocation().getDistrict());
        assertEquals(newBlock, value.getLocation().getBlock());
        assertEquals(newPanchayat, value.getLocation().getPanchayat());
        assertEquals(status, value.getLocation().getStatus());
        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateExistingFLWIfNameIsAnIncorrectFormat() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name~!";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        String newStatus = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, newStatus));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat", "NOT VERIFIED"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<>();
        frontLineWorkerList.add(frontLineWorker);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, newStatus));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(frontLineWorkerList);

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers, never()).createOrUpdate(captor.capture());
        assertEquals("Invalid name", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldUpdateNameAsEmptyIfFLWRequestHasNameAsBlankOrNull() {
        String msisdn = "919999888822";
        String name = " ";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat, "VALID"));
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(Long.valueOf(msisdn), "old name", Designation.ANM, new Location("district", "block", "panchayat", "NOT VERIFIED"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<FrontLineWorker>();
        frontLineWorkerList.add(frontLineWorker1);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(frontLineWorkerList);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "VALID"));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(StringUtils.EMPTY, frontLineWorker.getName());
        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldAddFLWIfFLWDoesNotExistToUpdateBasedOnMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String status = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat, status));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, status));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals((Long) Long.parseLong(prefixedMsisdn), value.getMsisdn());
    }

    @Test
    public void shouldNotUpdateIfNewMsisdnIsNotValid() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String status = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat, status));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, status));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        verify(allFrontLineWorkers, never()).getByMsisdn(Long.valueOf(msisdn));
        assertEquals("Invalid msisdn", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldUpdateDesignationAsInvalidIfNewDesignationIsNotValid() {
        final String msisdn = "919999888822";
        String name = "name";
        String designation = "invalid_designation";
        final String district = "district";
        final String block = "block";
        final String panchayat = "panchayat";
        final String status = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat, status));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(new ArrayList<FrontLineWorker>() {
            {
                add(new FrontLineWorker(Long.valueOf(msisdn), "oldName", Designation.ANM, new Location(district, block, panchayat, status)));
            }
        });
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, status));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker flw = captor.getValue();
        assertEquals((Long) Long.parseLong(msisdn), flw.getMsisdn());
        assertNull(flw.getDesignation());
        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewLocationDoesNotExist() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat, "VALID"));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        verify(allFrontLineWorkers, never()).getByMsisdn(Long.valueOf(msisdn));
        assertEquals("Invalid location", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldCreateANewRecordIfMsisdnIsBlankOrNull() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        String newStatus = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, newStatus));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, newStatus));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertNull(value.getMsisdn());
    }

    @Test
    public void shouldCreateANewRecordWhenMoreThanOneFLWWithTheGiveMsisdnExists() {
        final String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        final String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        String newStatus = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, newStatus));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, newStatus));
        when(allFrontLineWorkers.getByMsisdn(Long.parseLong(prefixedMsisdn))).thenReturn(new ArrayList<FrontLineWorker>() {
            {
                add(new FrontLineWorker(Long.parseLong(msisdn), newName, Designation.ASHA, new Location()));
                add(new FrontLineWorker(Long.parseLong(msisdn), "someOtherName", Designation.ASHA, new Location()));
            }
        });

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals(prefixedMsisdn, value.getMsisdn().toString());
        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldCreateANewRecordIfIncomingMsisdnIsBlankIrrespectiveOfTheNumberOfRecordsInDb() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        String newStatus = "VALID";
        FrontLineWorkerImportRequest frontLineWorkerWebRequest = new FrontLineWorkerImportRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat, newStatus));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat, newStatus));

        FrontLineWorkerImportResponse frontLineWorkerImportResponse = frontLineWorkerImportService.createOrUpdate(frontLineWorkerWebRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertNull(value.getMsisdn());
        assertEquals("FLW created/updated successfully", frontLineWorkerImportResponse.getMessage());
    }

    @Test
    public void shouldGetFrontLineWorkerById() {
        Long msisdn = 12L;

        frontLineWorkerImportService.getAllByMsisdn(msisdn);

        verify(allFrontLineWorkers).getByMsisdn(msisdn);
    }

    private void verifySync(FrontLineWorkerImportRequest frontLineWorkerImportRequest) {
        ArgumentCaptor<FrontLineWorker> frontLineWorkerArgumentCaptor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(syncService).syncFrontLineWorker(frontLineWorkerArgumentCaptor.capture());
        FrontLineWorker frontLineWorkerThatWasSynced = frontLineWorkerArgumentCaptor.getValue();
        assertEquals(frontLineWorkerImportRequest.getName(), frontLineWorkerThatWasSynced.getName());
        assertEquals(frontLineWorkerImportRequest.getDesignation(), frontLineWorkerThatWasSynced.getDesignation());
        assertEquals(frontLineWorkerImportRequest.getLocation().getDistrict(), frontLineWorkerThatWasSynced.getLocation().getDistrict());
        assertEquals(frontLineWorkerImportRequest.getLocation().getBlock(), frontLineWorkerThatWasSynced.getLocation().getBlock());
        assertEquals(frontLineWorkerImportRequest.getLocation().getPanchayat(), frontLineWorkerThatWasSynced.getLocation().getPanchayat());
    }
}