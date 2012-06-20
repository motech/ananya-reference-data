package org.motechproject.ananya.referencedata.web;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerControllerTest {
    private FrontLineWorkerController frontLineWorkerController;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Captor
    private ArgumentCaptor<List<FrontLineWorkerRequest>> captor;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerController = new FrontLineWorkerController(frontLineWorkerService);
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

        frontLineWorkerController.createOrUpdate(frontLineWorkerRequest);

        verify(frontLineWorkerService).createOrUpdate(any(FrontLineWorkerRequest.class));
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
