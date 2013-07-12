package org.motechproject.ananya.referencedata.flw.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.http.client.service.HttpClientService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerSyncServiceTest {
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private SyncEndpointService syncEndpointService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    private FrontLineWorkerSyncService frontLineWorkerSyncService;

    @Before
    public void setUp() {
        frontLineWorkerSyncService = new FrontLineWorkerSyncService(httpClientService, syncEndpointService, allFrontLineWorkers);
    }

    @Test
    public void shouldSyncListOfFLW() {
        Location location = new Location("D1", "B1", "P1", "state", LocationStatus.VALID, null);
        long msisdn = 1L;
        final FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, location, VerificationStatus.SUCCESS.name());
        frontLineWorker.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker);
        String url1 = "url1";
        String url2 = "url2";
        ArrayList<SyncEndpoint> flwSyncUrls = new ArrayList<>();
        String apiKeyName1 = "someAPIKeyName1";
        String apiKeyName2 = "someAPIkeyName2";
        String apiKeyValue1 = "someAPIKey1";
        String apiKeyValue2 = "someAPIkey2";
        flwSyncUrls.add(new SyncEndpoint(url1, apiKeyName1, apiKeyValue1));
        flwSyncUrls.add(new SyncEndpoint(url2, apiKeyName2, apiKeyValue2));
        Map<String, String> headers1 = new HashMap<>();
        headers1.put(apiKeyName1, apiKeyValue1);
        Map<String, String> headers2 = new HashMap<>();
        headers2.put(apiKeyName2, apiKeyValue2);


        when(syncEndpointService.getFlwSyncEndpoints()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);
        verify(httpClientService).post(flwSyncUrls.get(0).getUrl(), frontLineWorkerSyncRequest, headers1);
        verify(httpClientService).post(flwSyncUrls.get(1).getUrl(), frontLineWorkerSyncRequest, headers2);
    }

    @Test
    public void shouldSyncFLWIfThereAreMultipleFLWsInDBButThisHasAStatus() {
        Location location = new Location("D1", "B1", "P1", "state", LocationStatus.VALID, null);
        long msisdn = 1L;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(msisdn, "name", Designation.ANM, location, VerificationStatus.SUCCESS.name());
        frontLineWorker1.setVerificationStatus(VerificationStatus.SUCCESS);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(msisdn, "name", Designation.ANM, location, VerificationStatus.SUCCESS.name());
        Map<String, String> headers = new HashMap<>();
        String apiKeyName = "someAPIKeyName";
        String apiKeyValue = "someAPIKeyValue";
        headers.put(apiKeyName, apiKeyValue);
        frontLineWorker1.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker1);
        frontLineWorkers.add(frontLineWorker2);
        SyncEndpoint syncEndpoint = new SyncEndpoint("url", apiKeyName, apiKeyValue);
        ArrayList<SyncEndpoint> flwSyncUrls = new ArrayList<>();
        flwSyncUrls.add(syncEndpoint);
        when(syncEndpointService.getFlwSyncEndpoints()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker1);
        verify(httpClientService).post(syncEndpoint.getUrl(), frontLineWorkerSyncRequest, headers);
    }

    @Test
    public void shouldNotSyncFLWIfThereAreMultipleFLWsInDBButWithoutStatus() {
        Location location = new Location("D1", "B1", "P1", "state", LocationStatus.VALID, null);
        long msisdn = 1L;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(msisdn, "name", Designation.ANM, location, VerificationStatus.SUCCESS.name());
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(msisdn, "name", Designation.ANM, location, VerificationStatus.SUCCESS.name());

        frontLineWorker1.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker1);
        frontLineWorkers.add(frontLineWorker2);
        SyncEndpoint syncEndpoint = new SyncEndpoint("url", "apiKeyName", "apiKey");
        ArrayList<SyncEndpoint> flwSyncUrls = new ArrayList<>();
        flwSyncUrls.add(syncEndpoint);
        when(syncEndpointService.getFlwSyncEndpoints()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker1);
        verify(httpClientService, times(0)).post(syncEndpoint.getUrl(), frontLineWorkerSyncRequest);
    }
}
