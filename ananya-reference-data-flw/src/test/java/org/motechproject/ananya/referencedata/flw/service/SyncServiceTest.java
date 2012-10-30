package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.scheduler.context.EventContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SyncServiceTest {
    @Mock
    private EventContext eventContext;
    @Mock
    private AnanyaReferenceDataPropertiesService propertiesService;
    @Mock
    private LocationSyncService locationSyncService;

    private SyncService syncService;

    @Before
    public void setUp() {
        initMocks(this);
        syncService = new SyncService(eventContext, propertiesService,locationSyncService);
    }

    @Test
    public void shouldPublishFlwDataIntoQueue() {
        when(propertiesService.isSyncOn()).thenReturn(true);

        FrontLineWorker frontLineWorker = new FrontLineWorker(12L, "Aragorn", Designation.ANM, null);
        syncService.syncFrontLineWorker(frontLineWorker);

        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker);
    }

    @Test
    public void shouldNotPublishFlwDataIntoQueueIfSyncHasBeenTurnedOff() {
        when(propertiesService.isSyncOn()).thenReturn(false);
        FrontLineWorker frontLineWorker = new FrontLineWorker(12L, "Aragorn", Designation.ANM, null);

        syncService.syncFrontLineWorker(frontLineWorker);

        verify(eventContext, never()).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker);
    }

    @Test
    public void shouldSyncAllFrontLineWorkers() {
        when(propertiesService.isSyncOn()).thenReturn(true);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.ASHA, new Location());
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567891L, "name", Designation.ASHA, new Location());
        frontLineWorkers.addAll(Arrays.asList(frontLineWorker1, frontLineWorker2));

        syncService.syncAllFrontLineWorkers(frontLineWorkers);

        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker1);
        verify(eventContext).send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker2);
    }

    @Test
    public void shouldSyncAllLocations() {
        when(propertiesService.isSyncOn()).thenReturn(true);
        ArrayList<Location> locations = new ArrayList<>() ;

        syncService.syncAllLocations(locations);

        verify(locationSyncService).sync(locations);
    }
}
