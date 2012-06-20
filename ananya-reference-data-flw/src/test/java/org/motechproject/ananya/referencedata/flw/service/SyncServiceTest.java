package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.context.EventContext;

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
}
