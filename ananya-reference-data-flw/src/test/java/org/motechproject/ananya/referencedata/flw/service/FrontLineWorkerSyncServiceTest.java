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
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerSyncServiceTest {
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private SyncURLs syncURLs;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    private FrontLineWorkerSyncService frontLineWorkerSyncService;

    @Before
    public void setUp() {
        frontLineWorkerSyncService = new FrontLineWorkerSyncService(httpClientService, syncURLs, allFrontLineWorkers);
    }

    @Test
    public void shouldSyncListOfFLW() {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        long msisdn = 1L;
        final FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, location);
        frontLineWorker.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker);
        String url = "url";
        String url1 = "url1";
        ArrayList<String> flwSyncUrls = new ArrayList<>();
        flwSyncUrls.add(url);
        flwSyncUrls.add(url1);
        when(syncURLs.getFlwSyncEndpointUrls()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);
        verify(httpClientService).post(url, frontLineWorkerSyncRequest);
        verify(httpClientService).post(url1, frontLineWorkerSyncRequest);
    }

    @Test
    public void shouldSyncFLWIfThereAreMultipleFLWsInDBButThisHasAStatus() {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        long msisdn = 1L;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(msisdn, "name", Designation.ANM, location);
        frontLineWorker1.setVerificationStatus(VerificationStatus.SUCCESS);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(msisdn, "name", Designation.ANM, location);

        frontLineWorker1.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker1);
        frontLineWorkers.add(frontLineWorker2);
        String url = "url";
        ArrayList<String> flwSyncUrls = new ArrayList<>();
        flwSyncUrls.add(url);
        when(syncURLs.getFlwSyncEndpointUrls()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker1);
        verify(httpClientService).post(url, frontLineWorkerSyncRequest);
    }

    @Test
    public void shouldNotSyncFLWIfThereAreMultipleFLWsInDBButWithoutStatus() {
        Location location = new Location("D1", "B1", "P1", LocationStatus.VALID, null);
        long msisdn = 1L;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(msisdn, "name", Designation.ANM, location);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(msisdn, "name", Designation.ANM, location);

        frontLineWorker1.setLastModified(DateTime.now());
        List<FrontLineWorker> frontLineWorkers = new ArrayList();
        frontLineWorkers.add(frontLineWorker1);
        frontLineWorkers.add(frontLineWorker2);
        String url = "url";
        ArrayList<String> flwSyncUrls = new ArrayList<>();
        flwSyncUrls.add(url);
        when(syncURLs.getFlwSyncEndpointUrls()).thenReturn(flwSyncUrls);
        when(allFrontLineWorkers.getByMsisdn(msisdn)).thenReturn(frontLineWorkers);

        frontLineWorkerSyncService.sync(frontLineWorkers);

        FrontLineWorkerSyncRequest frontLineWorkerSyncRequest = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker1);
        verify(httpClientService,times(0)).post(url, frontLineWorkerSyncRequest);
    }
}
