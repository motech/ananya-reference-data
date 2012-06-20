package org.motechproject.ananya.referencedata.web.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.service.ScheduleSyncService;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SyncSchedulerTest {

    @Mock
    private ScheduleSyncService scheduleSyncService;

    @Test
    public void shouldInitateTheScheduler() {
        SyncScheduler syncScheduler = new SyncScheduler(scheduleSyncService);

        syncScheduler.initiateSync();

        verify(scheduleSyncService).scheduleSync();
    }
}
