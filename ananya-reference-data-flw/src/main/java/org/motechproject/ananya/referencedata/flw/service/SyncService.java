package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncService {
    private EventContext eventContext;
    private AnanyaReferenceDataPropertiesService propertiesService;

    Logger logger = Logger.getLogger(SyncService.class);

    @Autowired
    public SyncService(@Qualifier("eventContext") EventContext eventContext, AnanyaReferenceDataPropertiesService propertiesService) {
        this.eventContext = eventContext;
        this.propertiesService = propertiesService;
    }

    public void syncFrontLineWorker(Long msisdn) {
        if (propertiesService.isSyncOn()) {
            logger.info("Raising event to sync for msisdn: " + msisdn);
            eventContext.send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, msisdn);
        }
    }
}
