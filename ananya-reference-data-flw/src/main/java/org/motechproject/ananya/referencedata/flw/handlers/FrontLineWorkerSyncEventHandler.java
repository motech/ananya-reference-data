package org.motechproject.ananya.referencedata.flw.handlers;

import org.hibernate.exception.ExceptionUtils;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FrontLineWorkerSyncEventHandler {
    FrontLineWorkerService frontLineWorkerService;
    SyncService syncService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerSyncEventHandler.class);

    @Autowired
    public FrontLineWorkerSyncEventHandler(FrontLineWorkerService frontLineWorkerService, SyncService syncService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.syncService = syncService;
    }

    @MotechListener(subjects = SyncEventKeys.SCHEDULE_SYNC)
    public void scheduleFrontLineWorkerSync(MotechEvent motechEvent) {
        logger.info("Started sync");
        List<FrontLineWorker> allFLWsToBeSynced = frontLineWorkerService.getAllToBeSynced();
        for (FrontLineWorker frontLineWorker : allFLWsToBeSynced) {
            try {
                syncService.syncFrontLineWorker(frontLineWorker.getMsisdn());
                frontLineWorkerService.setSyncComplete(frontLineWorker);
                logger.info("Added FLW with msisdn : " + frontLineWorker.getMsisdn() + "to the queue.");
            } catch (Exception e) {
                logger.error("Failed to add queue item for msisdn : " + frontLineWorker.getMsisdn() + "with exception " + ExceptionUtils.getFullStackTrace(e));
            }
        }
        logger.info("Completed Sync");
    }
}
