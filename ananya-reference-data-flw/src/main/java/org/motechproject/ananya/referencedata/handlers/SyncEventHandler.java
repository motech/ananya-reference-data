package org.motechproject.ananya.referencedata.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.SyncEventKeys;
import org.motechproject.ananya.referencedata.mapper.FrontLineWorkerContractMapper;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Component
public class SyncEventHandler {

    private FrontLineWorkerService frontLineWorkerService;
    private Properties clientServicesProperties;
    private JsonHttpClient jsonHttpClient;

    public static final String KEY_FRONT_LINE_WORKER_CREATE_URL = "front.line.worker.create.url";

    Logger logger = Logger.getLogger(SyncEventHandler.class);

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
            logger.info("Sync for flwId: " + flwId);
            JsonHttpClient.Response response = jsonHttpClient.post(url, FrontLineWorkerContractMapper.mapFrom(frontLineWorker));
            logger.info(String.format("Status Code: %s | Response Body: %s", response.statusCode, response.body));
            checkForException(response);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    private void checkForException(JsonHttpClient.Response response) {
        if(HttpServletResponse.SC_INTERNAL_SERVER_ERROR == response.statusCode) {
            throw new IllegalStateException("Remote error: " + response.body);
        }
    }
}
