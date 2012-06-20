package org.motechproject.ananya.referencedata.flw.domain.jobs;

import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;

public class FrontLineWorkerSyncJob extends CronSchedulableJob {
    public FrontLineWorkerSyncJob(String cronExpression) {
        super(new MotechEvent(SyncEventKeys.SCHEDULE_SYNC), cronExpression);
    }
}
