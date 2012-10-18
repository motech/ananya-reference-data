package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.scheduler.context.EventContext;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SyncServiceTest {
    @Mock
    private EventContext eventContext;
    @Mock
    private AnanyaReferenceDataPropertiesService propertiesService;

    private SyncService syncService;

    @Before
    public void setUp(){
        initMocks(this);
        syncService = new SyncService(eventContext, propertiesService);
    }

    @Test
    public void shouldPublishFlwDataIntoQueue() {
        Long msisdn = 12L;
        when(propertiesService.isSyncOn()).thenReturn(true);

        syncService.syncFrontLineWorker(msisdn);
        
        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, msisdn);
    }

    @Test
    public void shouldNotPublishFlwDataIntoQueueIfSyncHasBeenTurnedOff() {
        Long msisdn = 12L;
        when(propertiesService.isSyncOn()).thenReturn(false);

        syncService.syncFrontLineWorker(msisdn);

        verify(eventContext, never()).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, msisdn);
    }

    @Test
    public void shouldSyncAllFrontLineWorkers() {
        Long msisdn1 = 1234567890L;
        Long msisdn2 = 1234567891L;
        when(propertiesService.isSyncOn()).thenReturn(true);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.ASHA, new Location());
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567891L, "name", Designation.ASHA, new Location());
        frontLineWorkers.add(frontLineWorker1);
        frontLineWorkers.add(frontLineWorker2);

        syncService.syncAllFrontLineWorkers(frontLineWorkers);

        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, msisdn1);
        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, msisdn2);
    }
}
