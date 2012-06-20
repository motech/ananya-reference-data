package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.repository.AllSyncJobs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleSyncService {
    AllSyncJobs allSyncJobs;

    @Autowired
    public ScheduleSyncService(AllSyncJobs allSyncJobs) {
        this.allSyncJobs = allSyncJobs;
    }

    public void scheduleSync() {
        allSyncJobs.addFrontLineWorkerSyncJob();
    }
}
