package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SyncServiceTest {
    @Mock
    private FrontLineWorkerSyncService frontLineWorkerSyncService;
    @Mock
    private AnanyaReferenceDataPropertiesService propertiesService;
    @Mock
    private LocationSyncService locationSyncService;

    private SyncService syncService;

    @Before
    public void setUp() {
        initMocks(this);
        syncService = new SyncService(propertiesService,locationSyncService, frontLineWorkerSyncService);
    }

    @Test
    public void shouldPublishFlwDataIntoQueue() {
        when(propertiesService.isSyncOn()).thenReturn(true);

        final FrontLineWorker frontLineWorker = new FrontLineWorker(12L, "Aragorn", Designation.ANM, null, VerificationStatus.SUCCESS.name());
        syncService.syncFrontLineWorker(frontLineWorker);

        verify(frontLineWorkerSyncService).sync(new ArrayList<FrontLineWorker>(){{add(frontLineWorker);}});
    }

    @Test
    public void shouldNotPublishFlwDataIntoQueueIfSyncHasBeenTurnedOff() {
        when(propertiesService.isSyncOn()).thenReturn(false);
        FrontLineWorker frontLineWorker = new FrontLineWorker(12L, "Aragorn", Designation.ANM, null, VerificationStatus.SUCCESS.name());

        syncService.syncFrontLineWorker(frontLineWorker);

        verify(frontLineWorkerSyncService, never()).sync(any(new ArrayList<FrontLineWorker>().getClass()));
    }

    @Test
    public void shouldSyncAllFrontLineWorkers() {
        when(propertiesService.isSyncOn()).thenReturn(true);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(1234567890L, "name", Designation.ASHA, new Location(), VerificationStatus.SUCCESS.name());
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(1234567891L, "name", Designation.ASHA, new Location(), VerificationStatus.SUCCESS.name());
        frontLineWorkers.addAll(Arrays.asList(frontLineWorker1, frontLineWorker2));

        syncService.syncAllFrontLineWorkers(frontLineWorkers);

        verify(frontLineWorkerSyncService).sync(frontLineWorkers);
    }

    @Test
    public void shouldSyncAllLocations() {
        when(propertiesService.isSyncOn()).thenReturn(true);
        Location location = new Location();

        syncService.syncLocation(location);

        verify(locationSyncService).sync(location);
    }
}
