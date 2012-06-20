package org.motechproject.ananya.referencedata.flw.repository;

import org.motechproject.ananya.referencedata.flw.domain.jobs.FrontLineWorkerSyncJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AllSyncJobs {
    private MotechSchedulerService schedulerService;
    private Properties referenceDataProperties;
    private final String CRON_EXPRESSION_KEY = "scheduler.cron.expression";

    @Autowired
    public AllSyncJobs(MotechSchedulerService schedulerService, Properties referenceDataProperties) {
        this.schedulerService = schedulerService;
        this.referenceDataProperties = referenceDataProperties;
    }

    public void addFrontLineWorkerSyncJob() {
        String cronExpression = (String) referenceDataProperties.get(CRON_EXPRESSION_KEY);
        schedulerService.safeScheduleJob(new FrontLineWorkerSyncJob(cronExpression));
    }
}
