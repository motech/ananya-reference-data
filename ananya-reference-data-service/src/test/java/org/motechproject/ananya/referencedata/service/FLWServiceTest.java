package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.repository.AllLocations;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.response.FLWResponse;

import static junit.framework.Assert.assertEquals;
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
    public void shouldNotAddFLWWithInvalidMSISDN() {
        String msisdn = "99998888";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

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

        FLWResponse flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid designation", flwResponse.getMessage());
        verify(allFrontLineWorkers, never()).add(Matchers.<FrontLineWorker>any());
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
}