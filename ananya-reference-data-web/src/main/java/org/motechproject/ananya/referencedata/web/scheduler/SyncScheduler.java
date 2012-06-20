package org.motechproject.ananya.referencedata.web.scheduler;

import org.motechproject.ananya.referencedata.flw.service.ScheduleSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {
    ScheduleSyncService scheduleSyncService;

    @Autowired
    public SyncScheduler(ScheduleSyncService scheduleSyncService) {
        this.scheduleSyncService = scheduleSyncService;
    }

    public void initiateSync() {
        scheduleSyncService.scheduleSync();
    }
}
