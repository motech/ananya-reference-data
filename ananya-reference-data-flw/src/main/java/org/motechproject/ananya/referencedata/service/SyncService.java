package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SyncService {
    private EventContext eventContext;
    private AnanyaReferenceDataPropertiesService propertiesService;

    @Autowired
    public SyncService(@Qualifier("eventContext") EventContext eventContext, AnanyaReferenceDataPropertiesService propertiesService) {
        this.eventContext = eventContext;
        this.propertiesService = propertiesService;
    }

    public void syncFrontLineWorker(Integer flwId) {
        if(propertiesService.isSyncOn()) {
            eventContext.send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, flwId);
        }
    }
}
