package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.FLWData;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.repository.AllFLWData;
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
    private AllFLWData allFLWData;

    private FLWService flwService;

    @Before
    public void setUp() {
        initMocks(this);
        flwService = new FLWService(allLocations, allFLWData);
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

        ArgumentCaptor<FLWData> captor = ArgumentCaptor.forClass(FLWData.class);
        verify(allFLWData).add(captor.capture());
        FLWData flwData = captor.getValue();

        assertEquals((Long) Long.parseLong(msisdn), flwData.getMsisdn());
        assertEquals(name, flwData.getName());
        assertEquals(designation, flwData.getDesignation());
        assertEquals(district, flwData.getLocation().getDistrict());
        assertEquals(block, flwData.getLocation().getBlock());
        assertEquals(panchayat, flwData.getLocation().getPanchayat());
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
        verify(allFLWData, never()).add(Matchers.<FLWData>any());

        msisdn = "9A99888822";
        flwRequest = new FLWRequest(msisdn, name, designation, district, block, panchayat);

        flwResponse = flwService.add(flwRequest);

        assertEquals("Invalid msisdn", flwResponse.getMessage());
        verify(allFLWData, never()).add(Matchers.<FLWData>any());

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
        verify(allFLWData, never()).add(Matchers.<FLWData>any());
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
        verify(allFLWData, never()).add(Matchers.<FLWData>any());
    }
}