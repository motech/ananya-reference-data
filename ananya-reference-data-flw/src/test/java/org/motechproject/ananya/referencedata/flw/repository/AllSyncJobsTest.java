package org.motechproject.ananya.referencedata.flw.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllSyncJobsTest {

    @Mock
    private MotechSchedulerService schedulerService;

    @Test
    public void shouldScheduleJobToAddFLWToQueue() {
        AllSyncJobs allSyncJobs = new AllSyncJobs(schedulerService);

        allSyncJobs.addFrontLineWorkerSyncJob();

        verify(schedulerService).safeScheduleJob((CronSchedulableJob) any());
    }
}
