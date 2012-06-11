package org.motechproject.ananya.referencedata.handlers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.*;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-flw.xml")
public class SyncEventHandlerTest {
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private JsonHttpClient jsonHttpClient;
    @Autowired
    private Properties clientServicesProperties;
    private SyncEventHandler syncEventHandler;

    @Before
    public void setUp() {
        initMocks(this);
        syncEventHandler = new SyncEventHandler(frontLineWorkerService, jsonHttpClient, clientServicesProperties);
    }

    @Test
    public void shouldInvokeClientServiceWithFrontLineWorkerData() throws IOException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String msisdn = "1234";
        Integer id = 12;
        Location location = new Location("district1", "block1", "panchayat1");
        DateTime lastModified = DateTime.now();
        location.setLastModified(lastModified);
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.parseLong(msisdn), "name1", Designation.ANM, location);
        frontLineWorker.setLastModified(lastModified);
        when(frontLineWorkerService.getById(id)).thenReturn(frontLineWorker);
        when(jsonHttpClient.post(Matchers.<String>any(), Matchers.<Object>any())).thenReturn(new JsonHttpClient.Response(HttpServletResponse.SC_ACCEPTED, "{success}"));

        parameters.put("0", id);
        syncEventHandler.handleSyncFrontLineWorker(new MotechEvent(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, parameters));

        ArgumentCaptor<FrontLineWorkerContract> captor = ArgumentCaptor.forClass(FrontLineWorkerContract.class);
        verify(jsonHttpClient).post(eq("https://localhost/ananya/flw"), captor.capture());
        FrontLineWorkerContract value = captor.getValue();
        assertEquals(msisdn, value.getMsisdn());
    }

    @Test
    @ExpectedException(value = IllegalStateException.class)
    public void shouldCryWhenResponseStatusCodeIs500() throws IOException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String msisdn = "1234";
        Integer id = 12;
        Location location = new Location("district1", "block1", "panchayat1");
        DateTime lastModified = DateTime.now();
        location.setLastModified(lastModified);
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.parseLong(msisdn), "name1", Designation.ANM, location);
        frontLineWorker.setLastModified(lastModified);
        when(frontLineWorkerService.getById(id)).thenReturn(frontLineWorker);
        when(jsonHttpClient.post(Matchers.<String>any(), Matchers.<Object>any())).thenReturn(new JsonHttpClient.Response(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ExceptionTrace"));

        parameters.put("0", id);
        syncEventHandler.handleSyncFrontLineWorker(new MotechEvent(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, parameters));
    }
}
