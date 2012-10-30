package org.motechproject.ananya.referencedata.flw.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerSyncRequestMapper;
import org.motechproject.http.client.service.HttpClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerSyncServiceTest {

    @Mock
    private HttpClientService httpClientService;

    @Mock
    private Properties clientServicesProperties;

    @Test
    public void shouldSyncListOfFLW() {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID.name(), null);
        final FrontLineWorker frontLineWorker = new FrontLineWorker(1L, "name", Designation.ANM, location);
        frontLineWorker.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker);
        FrontLineWorkerSyncService frontLineWorkerSyncService = new FrontLineWorkerSyncService(httpClientService, clientServicesProperties);
        String url = "url";
        when(clientServicesProperties.getProperty(SyncURLs.KEY_FRONT_LINE_WORKER_CREATE_URL)).thenReturn(url);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);
        verify(httpClientService).post(url, frontLineWorkerSyncRequest);
    }
}
