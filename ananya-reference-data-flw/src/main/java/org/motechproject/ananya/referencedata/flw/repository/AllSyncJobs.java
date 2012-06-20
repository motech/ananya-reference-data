package org.motechproject.ananya.referencedata.flw.repository;

import org.motechproject.ananya.referencedata.flw.domain.jobs.FrontLineWorkerSyncJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllSyncJobs {
    private MotechSchedulerService schedulerService;

    @Autowired
    public AllSyncJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void addFrontLineWorkerSyncJob() {
        schedulerService.safeScheduleJob(new FrontLineWorkerSyncJob());
    }
}
