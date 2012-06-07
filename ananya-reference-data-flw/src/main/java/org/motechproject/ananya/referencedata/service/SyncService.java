package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
import org.motechproject.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SyncService {
    private EventContext eventContext;

    @Autowired
    public SyncService(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void syncFrontLineWorker(Integer flwId) {
        eventContext.send(SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE, flwId);
    }
}
