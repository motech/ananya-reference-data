package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncService {
    private EventContext eventContext;
    private AnanyaReferenceDataPropertiesService propertiesService;
    private LocationSyncService locationSyncService;

    Logger logger = Logger.getLogger(SyncService.class);

    @Autowired
    public SyncService(@Qualifier("eventContext") EventContext eventContext, AnanyaReferenceDataPropertiesService propertiesService, LocationSyncService locationSyncService) {
        this.eventContext = eventContext;
        this.propertiesService = propertiesService;
        this.locationSyncService = locationSyncService;
    }

    public void syncFrontLineWorker(FrontLineWorker frontLineWorker) {
        if (propertiesService.isSyncOn()) {
            logger.info("Raising event to sync for msisdn: " + frontLineWorker.getMsisdn());
            eventContext.send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker);
        }
    }

    public void syncAllFrontLineWorkers(List<FrontLineWorker> frontLineWorkers) {
        if (propertiesService.isSyncOn()) {
            for (FrontLineWorker frontLineWorker : frontLineWorkers) {
                logger.info("Raising event to sync for msisdn: " + frontLineWorker.getMsisdn());
                eventContext.send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, frontLineWorker);
            }
        }
    }

    public void syncAllLocations(ArrayList<Location> locations) {
        if (propertiesService.isSyncOn()) {
                locationSyncService.sync(locations);
        }
    }
}
