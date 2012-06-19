package org.motechproject.ananya.referencedata.flw.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FrontLineWorkerServiceTest {

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

    private FrontLineWorkerService frontLineWorkerService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allLocations, allFrontLineWorkers, syncService);
    }

    @Test
    public void shouldInvokeSyncServiceToPushFlwChangesWhenFlwIsUpdated() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<FrontLineWorker>();
        frontLineWorkerList.add(frontLineWorker);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(frontLineWorkerList);

        frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        verify(syncService).syncFrontLineWorker(any(Long.class));
    }

    @Test
    public void shouldInvokeSyncServiceToPushFlwChangesWhenFlwIsAdded() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));

        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(Collections.<FrontLineWorker>emptyList());

        frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        verify(syncService).syncFrontLineWorker(any(Long.class));
    }

    @Test
    public void shouldAddFLWsInBulk() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454522";
        String msisdn2 = "911234454623";
        FrontLineWorkerRequest frontLineWorkerRequest1 = new FrontLineWorkerRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat));
        FrontLineWorkerRequest frontLineWorkerRequest2 = new FrontLineWorkerRequest(msisdn2, "name", "ASHA", new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequests.add(frontLineWorkerRequest1);
        frontLineWorkerRequests.add(frontLineWorkerRequest2);

        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);

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
        FrontLineWorkerRequest frontLineWorkerRequest1 = new FrontLineWorkerRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        FrontLineWorker expectedFLW = new FrontLineWorker();
        List<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        frontLineWorkers.add(expectedFLW);
        when(allFrontLineWorkers.getByMsisdn(Long.parseLong(msisdn1))).thenReturn(frontLineWorkers);
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequests.add(frontLineWorkerRequest1);

        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertTrue(actualFLWs.contains(expectedFLW));
        assertEquals(1, actualFLWs.size());
        verify(syncService).syncAllFrontLineWorkers(anotherCaptor.capture());
        List<FrontLineWorker> actualFLWsForSync = anotherCaptor.getValue();
        assertEquals(actualFLWsForSync , actualFLWs);
    }

    @Test
    public void shouldAddRecordsAndNotUpdateIfThereAreDuplicatesWithinTheRequestList() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "12344545";
        FrontLineWorkerRequest frontLineWorkerRequest1 = new FrontLineWorkerRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat));
        FrontLineWorkerRequest frontLineWorkerRequest2 = new FrontLineWorkerRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequests.add(frontLineWorkerRequest1);
        frontLineWorkerRequests.add(frontLineWorkerRequest2);

        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertEquals(2, actualFLWs.size());
    }

    @Test
    public void shouldUpdateExistingFLWBasedOnMsisdn() {
        String msisdn = "9999888822";
        String prefixedMsisdn = "91" + msisdn;
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<FrontLineWorker>();
        frontLineWorkerList.add(frontLineWorker);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(frontLineWorkerList);

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());

        FrontLineWorker value = captor.getValue();

        assertEquals((Long) Long.parseLong(prefixedMsisdn), value.getMsisdn());
        assertEquals(newName, value.getName());
        assertEquals(newDesignation, value.getDesignation());
        assertEquals(newDistrict, value.getLocation().getDistrict());
        assertEquals(newBlock, value.getLocation().getBlock());
        assertEquals(newPanchayat, value.getLocation().getPanchayat());
        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<FrontLineWorker>();
        frontLineWorkerList.add(frontLineWorker);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(prefixedMsisdn))).thenReturn(frontLineWorkerList);

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers, never()).createOrUpdate(captor.capture());
        assertEquals("Invalid name", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldUpdateNameAsEmptyIfFLWRequestHasNameAsBlankOrNull() {
        String msisdn = "919999888822";
        String name = " ";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(Long.valueOf(msisdn), "old name", Designation.ANM, new Location("district", "block", "panchayat"));
        List<FrontLineWorker> frontLineWorkerList = new ArrayList<FrontLineWorker>();
        frontLineWorkerList.add(frontLineWorker1);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(frontLineWorkerList);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(StringUtils.EMPTY, frontLineWorker.getName());
        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        verify(allFrontLineWorkers, never()).getByMsisdn(Long.valueOf(msisdn));
        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldUpdateDesignationAsInvalidIfNewDesignationIsNotValid() {
        final String msisdn = "919999888822";
        String name = "name";
        String designation = "invalid_designation";
        final String district = "district";
        final String block = "block";
        final String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(new ArrayList<FrontLineWorker>() {
            {
                add(new FrontLineWorker(Long.valueOf(msisdn), "oldName", Designation.ANM, new Location(district, block, panchayat)));
            }
        });
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals((Long) Long.parseLong(msisdn), value.getMsisdn());
        assertEquals(Designation.INVALID.name(), value.getDesignation());
        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewLocationDoesNotExist() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        verify(allFrontLineWorkers, never()).getByMsisdn(Long.valueOf(msisdn));
        assertEquals("Invalid location", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldCreateANewRecordIfMsisdnIsBlankOrNull() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getByMsisdn(Long.parseLong(prefixedMsisdn))).thenReturn(new ArrayList<FrontLineWorker>() {
            {
                add(new FrontLineWorker(Long.parseLong(msisdn), newName, Designation.ASHA, new Location()));
                add(new FrontLineWorker(Long.parseLong(msisdn), "someOtherName", Designation.ASHA, new Location()));
            }
        });

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals(prefixedMsisdn, value.getMsisdn().toString());
        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldCreateANewRecordIfIncomingMsisdnIsBlankIrrespectiveOfTheNumberOfRecordsInDb() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation, new LocationRequest(newDistrict, newBlock, newPanchayat));
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdate(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertNull(value.getMsisdn());
        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldGetFrontLineWorkerById() {
        Long msisdn = 12L;

        frontLineWorkerService.getAllByMsisdn(msisdn);

        verify(allFrontLineWorkers).getByMsisdn(msisdn);
    }
}