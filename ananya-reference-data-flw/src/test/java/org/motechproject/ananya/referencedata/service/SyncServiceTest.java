package org.motechproject.ananya.referencedata.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
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
        Integer flwId = 12;
        when(propertiesService.isSyncOn()).thenReturn(true);

        syncService.syncFrontLineWorker(flwId);
        
        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, flwId);
    }

    @Test
    public void shouldNotPublishFlwDataIntoQueueIfSyncHasBeenTurnedOff() {
        int flwId = 12;
        when(propertiesService.isSyncOn()).thenReturn(false);

        syncService.syncFrontLineWorker(flwId);

        verify(eventContext, never()).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, flwId);
    }
}
