package org.motechproject.ananya.referencedata.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.FrontLineWorkerResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FrontLineWorkerServiceTest {

    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Captor
    ArgumentCaptor<List<FrontLineWorker>> captor;

    private FrontLineWorkerService frontLineWorkerService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allLocations, allFrontLineWorkers);
    }

    @Test
    public void shouldValidateAndAddFLW() {
        String msisdn = "919999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals((Long) Long.parseLong(msisdn), frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(district, frontLineWorker.getLocation().getDistrict());
        assertEquals(block, frontLineWorker.getLocation().getBlock());
        assertEquals(panchayat, frontLineWorker.getLocation().getPanchayat());
        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotAddFLWIfFLWWithSameMsisdnExists() {
        String msisdn = "919999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(new FrontLineWorker());

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("FLW already exists with the same MSISDN number", frontLineWorkerResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldNotAddFLWWithInvalidMSISDN() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());

        msisdn = "9A99888822";
        frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));

        frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());

    }

    @Test
    public void shouldNotAddFlWWithInvalidName() {
        String msisdn = "919999888822";
        String name = "n@me";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("Invalid name", frontLineWorkerResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldAddNameAsEmptyIfFLWRequestHasNameAsBlankOrNull() {
        String msisdn = "919999888822";
        String name = " ";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(StringUtils.EMPTY, frontLineWorker.getName());
        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldAddFLWWithDesignationAsInvalidIfDesignationIsInvalid() {
        String msisdn = "919999888822";
        String name = "name";
        String designation = "Random";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        assertEquals(Designation.INVALID.name(), captor.getValue().getDesignation());
    }

    @Test
    public void shouldPrefixWith91IfMsisdnIsATenDigitNumber() {
        String msisdn = "1234567890";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals((Long) 911234567890L, frontLineWorker.getMsisdn());
        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldAddFLWEvenIfMsisdnIsBlankOrNull() {
        String msisdn = "";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(null, frontLineWorker.getMsisdn());
        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldAddFLWsInBulk() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "12344545";
        String msisdn2 = "12344546";
        FrontLineWorkerRequest frontLineWorkerRequest1 = new FrontLineWorkerRequest(msisdn1, "name", "ASHA",  new LocationRequest(district, block, panchayat));
        FrontLineWorkerRequest frontLineWorkerRequest2 = new FrontLineWorkerRequest(msisdn2, "name", "ASHA",  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequests.add(frontLineWorkerRequest1);
        frontLineWorkerRequests.add(frontLineWorkerRequest2);

        frontLineWorkerService.addAllWithoutValidations(frontLineWorkerRequests);

        verify(allFrontLineWorkers).addAll(captor.capture());
        List<FrontLineWorker> frontLineWorkers = captor.getValue();
        assertEquals(msisdn1, frontLineWorkers.get(0).getMsisdn().toString());
        assertEquals(msisdn2, frontLineWorkers.get(1).getMsisdn().toString());
    }

    @Test
    public void shouldNotAddFLWIfLocationIsNotAvailable() {
        String msisdn = "919999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "~district";
        String block = "~block";
        String panchayat = "~panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));

        when(allLocations.getFor(district, block, panchayat)).thenReturn(null);

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertEquals("Invalid location", frontLineWorkerResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldNotAddFLWIfInvalidMsisdnAndLocationIsNotAvailable() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "~district";
        String block = "~block";
        String panchayat = "~panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(null);

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.add(frontLineWorkerRequest);

        assertTrue(frontLineWorkerResponse.getMessage().contains("Invalid msisdn"));
        assertTrue(frontLineWorkerResponse.getMessage().contains("Invalid location"));
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
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
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(prefixedMsisdn))).thenReturn(new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat")));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());

        FrontLineWorker value = captor.getValue();

        assertEquals((Long)Long.parseLong(prefixedMsisdn), value.getMsisdn());
        assertEquals(newName, value.getName());
        assertEquals(newDesignation, value.getDesignation());
        assertEquals(newDistrict, value.getLocation().getDistrict());
        assertEquals(newBlock, value.getLocation().getBlock());
        assertEquals(newPanchayat, value.getLocation().getPanchayat());
        assertEquals("FLW updated successfully", frontLineWorkerResponse.getMessage());
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
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(prefixedMsisdn))).thenReturn(new FrontLineWorker(Long.valueOf(prefixedMsisdn), "name", Designation.ANM, new Location("district", "block", "panchayat")));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers, never()).update(captor.capture());
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
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(new FrontLineWorker(Long.valueOf(msisdn), "old name", Designation.ANM, new Location("district", "block", "panchayat")));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(StringUtils.EMPTY, frontLineWorker.getName());
        assertEquals("FLW updated successfully", frontLineWorkerResponse.getMessage());
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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals((Long)Long.parseLong(prefixedMsisdn), value.getMsisdn());
    }

    @Test
    public void shouldNotUpdateIfNewMsisdnIsNotValid() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        verify(allFrontLineWorkers,never()).getFor(Long.valueOf(msisdn));
        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldUpdateDesignationAsInvalidIfNewDesignationIsNotValid() {
        String msisdn = "919999888822";
        String name = "name";
        String designation = "invalid_designation";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(new FrontLineWorker(Long.valueOf(msisdn), "oldName", Designation.ANM, new Location(district, block, panchayat)));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        FrontLineWorker value = captor.getValue();
        assertEquals((Long)Long.parseLong(msisdn), value.getMsisdn());
        assertEquals(Designation.INVALID.name(), value.getDesignation());
        assertEquals("FLW updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewLocationDoesNotExist() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation,  new LocationRequest(district, block, panchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        verify(allFrontLineWorkers,never()).getFor(Long.valueOf(msisdn));
        assertEquals("Invalid location", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateWhenMsisdnInTheRequestIsBlankOrNull() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, newName, newDesignation,  new LocationRequest(newDistrict, newBlock, newPanchayat));

        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));

        FrontLineWorkerResponse frontLineWorkerResponse = frontLineWorkerService.update(frontLineWorkerRequest);

        verify(allFrontLineWorkers, never()).getFor(Matchers.<Long>any());
        verify(allFrontLineWorkers,never()).update(any(FrontLineWorker.class));
        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
    }
}