package org.motechproject.ananya.referencedata.handlers;

import org.apache.commons.lang.StringUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class SyncEventHandler {

    private FrontLineWorkerService frontLineWorkerService;
    private Properties clientServicesProperties;
    private Properties referenceDataProperties;
    private JsonHttpClient jsonHttpClient;

    public static final String KEY_FRONT_LINE_WORKER_CREATE_URL = "front.line.worker.create.url";
    public static final String ANANYA_API_KEY = "ananya.api.key";

    Logger logger = Logger.getLogger(SyncEventHandler.class);

    @Autowired
    public SyncEventHandler(FrontLineWorkerService frontLineWorkerService, JsonHttpClient jsonHttpClient, Properties clientServicesProperties, Properties referenceDataProperties) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.jsonHttpClient = jsonHttpClient;
        this.clientServicesProperties = clientServicesProperties;
        this.referenceDataProperties = referenceDataProperties;
    }

    @MotechListener(subjects = {SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE})
    public void handleSyncFrontLineWorker(MotechEvent event) {
        Long msisdn = (Long) event.getParameters().get("0");
        List<FrontLineWorker> frontLineWorkers = frontLineWorkerService.getAllByMsisdn(msisdn);
        String url = (String) clientServicesProperties.get(KEY_FRONT_LINE_WORKER_CREATE_URL);

        if (frontLineWorkers.size() != 1 || frontLineWorkers.get(0).getMsisdn() == null)
            return;

        try {
            logger.info("Sync for msisdn: " + msisdn);
            Map<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("APIKey", (String) referenceDataProperties.get(ANANYA_API_KEY));
            JsonHttpClient.Response response = jsonHttpClient.post(url, FrontLineWorkerContractMapper.mapFrom(frontLineWorkers.get(0)), requestHeaders);
            logger.info(String.format("Status Code: %s | Response Body: %s", response.statusCode, response.body));
            checkForException(response);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    private void checkForException(JsonHttpClient.Response response) {
        if (HttpServletResponse.SC_OK != response.statusCode || !StringUtils.containsIgnoreCase((String) response.body, "Created/Updated FLW record")) {
            throw new IllegalStateException("Remote error: " + response.body);
        }
    }
}
