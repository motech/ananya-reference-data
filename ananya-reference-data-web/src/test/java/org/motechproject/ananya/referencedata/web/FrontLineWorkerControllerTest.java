package org.motechproject.ananya.referencedata.web;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerControllerTest {
    private FrontLineWorkerController frontLineWorkerController;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;

    @Before
    public void setUp(){
        initMocks(this);
        frontLineWorkerController = new FrontLineWorkerController(frontLineWorkerService);
    }

    @Test
    public void shouldCreateNewFLW() {
        String msisdn = "9999888822";
        String name = "name";
        String designation = "ASHA";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        frontLineWorkerController.create(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorkerRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerRequest.class);
        verify(frontLineWorkerService).add(captor.capture());
        FrontLineWorkerRequest captorValue = captor.getValue();

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
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, name, designation, new LocationRequest(district, block, panchayat));

        frontLineWorkerController.update(frontLineWorkerRequest);

        verify(frontLineWorkerService).update(any(FrontLineWorkerRequest.class));
    }

    @Test
    public void shouldReturnExceptionResponseAndSetStatusTo500() {
        String errorMessage = "Foo error";
        MockHttpServletResponse response = new MockHttpServletResponse();
        ExceptionResponse exceptionResponse = new Gson().fromJson(frontLineWorkerController.handleException(new IllegalArgumentException(errorMessage), response), ExceptionResponse.class);

        assertEquals(errorMessage, exceptionResponse.getMessage());
        assertNotNull(exceptionResponse.getTrace());
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals("application/json", response.getHeader("Content-Type"));
    }
}
