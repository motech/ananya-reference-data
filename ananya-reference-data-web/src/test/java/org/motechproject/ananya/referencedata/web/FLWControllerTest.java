package org.motechproject.ananya.referencedata.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.request.FLWRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.service.FLWService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FLWControllerTest {
    private FLWController flwController;

    @Mock
    private FLWService flwService;

    @Before
    public void setUp(){
        initMocks(this);
        flwController = new FLWController(flwService);
    }

    @Test
    public void shouldCreateNewFLW() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        flwController.create(flwRequest);

        ArgumentCaptor<FLWRequest> captor = ArgumentCaptor.forClass(FLWRequest.class);
        verify(flwService).add(captor.capture());
        FLWRequest captorValue = captor.getValue();

        assertEquals(msisdn, captorValue.getMsisdn());
        assertEquals(name, captorValue.getName());
        assertEquals(designation, captorValue.getDesignation());
        assertEquals(district, captorValue.getLocation().getDistrict());
        assertEquals(block, captorValue.getLocation().getBlock());
        assertEquals(panchayat, captorValue.getLocation().getPanchayat());
    }

    @Test
    public void shouldUpdateAnExistingFLW() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FLWRequest flwRequest = new FLWRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        flwController.update(flwRequest);

        verify(flwService).update(any(FLWRequest.class));
    }
}
