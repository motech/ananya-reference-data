package org.motechproject.ananya.referencedata.web;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerControllerTest {
    private FrontLineWorkerController frontLineWorkerController;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Captor
    private ArgumentCaptor<List<FrontLineWorkerWebRequest>> captor;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerController = new FrontLineWorkerController(frontLineWorkerService);
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

    @Test(expected = ValidationException.class)
    public void shouldReturnExceptionResponseAndSetStatusTo500IfValidationErrorOccurs(){
        frontLineWorkerController.updateVerifiedFlw(new FrontLineWorkerWebRequest("guid",null,null));
    }
    
    @Test
    public void shouldUpdateVerificationStatusForInvalidFLW() {
        String reason = "reason";
        String guid = "abcd1234";
        String verificationStatus = "INVALID";
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(guid, verificationStatus, reason);

        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest);

        verify(frontLineWorkerService).updateVerifiedFlw(frontLineWorkerWebRequest);
    }
}
