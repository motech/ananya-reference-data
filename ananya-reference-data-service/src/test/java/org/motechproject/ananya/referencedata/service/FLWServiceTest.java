package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FLWServiceTest {

    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;

    private FLWService flwService;

    @Before
    public void setUp() {
        initMocks(this);
        flwService = new FLWService(allLocations, allFrontLineWorkers);
    }

    @Test
    public void shouldValidateAndAddFLW() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.add(flwRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals((Long) Long.parseLong(msisdn), frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(district, frontLineWorker.getLocation().getDistrict());
        assertEquals(block, frontLineWorker.getLocation().getBlock());
        assertEquals(panchayat, frontLineWorker.getLocation().getPanchayat());
        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldNotAddFLWIfFLWWithSameMsisdnExists() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(new FrontLineWorker());

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertEquals("FLW already exists", flwResponse.getMessage());
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
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid msisdn", flwResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());

        msisdn = "9A99888822";
        flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

        flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid msisdn", flwResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());

    }

    @Test
    public void shouldNotAddFLWIfDesignationIsInvalid() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "Random";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid designation", flwResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldAddFLWEvenIfMsisdnIsBlankOrNull() {
        String msisdn = "";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.add(flwRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();

        assertEquals(null, frontLineWorker.getMsisdn());
        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldNotAddFLWIfLocationIsNotAvailable() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "~district";
        String block = "~block";
        String panchayat = "~panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

        when(allLocations.getFor(district, block, panchayat)).thenReturn(null);

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid location", flwResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldNotAddFLWIfInvalidMsisdnInvalidDesignationAndLocationIsNotAvailable() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "Random";
        String district = "~district";
        String block = "~block";
        String panchayat = "~panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(null);

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertTrue(flwResponse.getMessage().contains("Invalid msisdn"));
        assertTrue(flwResponse.getMessage().contains("Invalid designation"));
        assertTrue(flwResponse.getMessage().contains("Invalid location"));
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
    }

    @Test
    public void shouldUpdateExistingFLWBasedOnMsisdn() {
        String msisdn = "9999888822";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FLWRequest flwRequest = new FLWRequest(msisdn, newName, newDesignation, newDistrict, newBlock, newPanchayat);
        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(new FrontLineWorker(Long.valueOf(msisdn), "name", Designation.ANM, new Location("district", "block", "panchayat")));

        FLWResponse flwResponse = flwService.update(flwRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());

        FrontLineWorker value = captor.getValue();

        assertEquals((Long)Long.parseLong(msisdn), value.getMsisdn());
        assertEquals(newName, value.getName());
        assertEquals(newDesignation, value.getDesignation());
        assertEquals(newDistrict, value.getLocation().getDistrict());
        assertEquals(newBlock, value.getLocation().getBlock());
        assertEquals(newPanchayat, value.getLocation().getPanchayat());
        assertEquals("FLW updated successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldAddFLWIfFLWDoesNotExistToUpdateBasedOnMsisdn() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allFrontLineWorkers.getFor(Long.valueOf(msisdn))).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));
        
        FLWResponse flwResponse = flwService.update(flwRequest);

        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewMsisdnIsNotValid() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.update(flwRequest);

        verify(allFrontLineWorkers,never()).getFor(Long.valueOf(msisdn));
        assertEquals("Invalid msisdn", flwResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewDesignationIsNotValid() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "invalid_designation";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat));

        FLWResponse flwResponse = flwService.update(flwRequest);

        verify(allFrontLineWorkers,never()).getFor(Long.valueOf(msisdn));
        assertEquals("Invalid designation", flwResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateIfNewLocationDoesNotExist() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

        FLWResponse flwResponse = flwService.update(flwRequest);

        verify(allFrontLineWorkers,never()).getFor(Long.valueOf(msisdn));
        assertEquals("Invalid location", flwResponse.getMessage());
    }

    @Test
    public void shouldNotUpdateWhenMsisdnInTheRequestIsBlankOrNull() {
        String msisdn = "";
        String newName = "new name";
        String newDesignation = "ASHA";
        String newDistrict = "district1";
        String newBlock = "block1";
        String newPanchayat = "panchayat1";
        FLWRequest flwRequest = new FLWRequest(msisdn, newName, newDesignation, newDistrict, newBlock, newPanchayat);

        when(allLocations.getFor(newDistrict, newBlock, newPanchayat)).thenReturn(new Location(newDistrict, newBlock, newPanchayat));

        FLWResponse flwResponse = flwService.update(flwRequest);

        verify(allFrontLineWorkers, never()).getFor(Matchers.<Long>any());
        verify(allFrontLineWorkers,never()).update(any(FrontLineWorker.class));
        assertEquals("Invalid msisdn", flwResponse.getMessage());
    }
}