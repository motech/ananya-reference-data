package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.repository.AllSyncJobs;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleSyncServiceTest {
    @Mock
    private AllSyncJobs allSyncJobs;

    @Test
    public void shouldInitiateSync() {
        ScheduleSyncService scheduleSyncService = new ScheduleSyncService(allSyncJobs);

        scheduleSyncService.scheduleSync();

        verify(allSyncJobs).addFrontLineWorkerSyncJob();
    }
}
