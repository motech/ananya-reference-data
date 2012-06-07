package org.motechproject.ananya.referencedata.handlers;

import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
import org.motechproject.ananya.referencedata.mapper.FrontLineWorkerContractMapper;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class SyncEventHandler {

    private FrontLineWorkerService frontLineWorkerService;
    private Properties clientServicesProperties;
    private JsonHttpClient jsonHttpClient;

    public static final String KEY_FRONT_LINE_WORKER_CREATE_URL = "front.line.worker.create.url";

    @Autowired
    public SyncEventHandler(FrontLineWorkerService frontLineWorkerService, JsonHttpClient jsonHttpClient, Properties clientServicesProperties) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.jsonHttpClient = jsonHttpClient;
        this.clientServicesProperties = clientServicesProperties;
    }

    @MotechListener(subjects = {SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE})
    public void handleSyncFrontLineWorker(MotechEvent event) {
        Integer flwId = (Integer) event.getParameters().get("0");
        FrontLineWorker frontLineWorker = frontLineWorkerService.getById(flwId);
        String url = (String) clientServicesProperties.get(KEY_FRONT_LINE_WORKER_CREATE_URL);
        try {
            jsonHttpClient.post(url, FrontLineWorkerContractMapper.mapFrom(frontLineWorker));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
