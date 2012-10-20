package org.motechproject.ananya.referencedata.flw.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncEventKeys;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerContractMapper;
import org.motechproject.ananya.referencedata.flw.service.JsonHttpClient;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class SyncEventHandler {

    private Properties clientServicesProperties;
    private Properties referenceDataProperties;
    private JsonHttpClient jsonHttpClient;

    private final String KEY_FRONT_LINE_WORKER_CREATE_URL = "front.line.worker.create.url";
    private final String ANANYA_API_KEY = "ananya.api.key";

    Logger logger = Logger.getLogger(SyncEventHandler.class);

    @Autowired
    public SyncEventHandler(JsonHttpClient jsonHttpClient, Properties clientServicesProperties, Properties referenceDataProperties) {
        this.jsonHttpClient = jsonHttpClient;
        this.clientServicesProperties = clientServicesProperties;
        this.referenceDataProperties = referenceDataProperties;
    }

    @MotechListener(subjects = {SyncEventKeys.FRONT_LINE_WORKER_DATA_MESSAGE})
    public void handleSyncFrontLineWorker(MotechEvent event) {
        FrontLineWorker frontLineWorker = (FrontLineWorker) event.getParameters().get("0");
        String url = (String) clientServicesProperties.get(KEY_FRONT_LINE_WORKER_CREATE_URL);

        if (frontLineWorker.getMsisdn() == null) {
            logger.info("Ignoring sync for flw name:"+frontLineWorker.getName()+" since msisdn is null ");
            return;
        }

        try {
            logger.info("Sync for msisdn: " + frontLineWorker.getMsisdn());
            Map<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("APIKey", (String) referenceDataProperties.get(ANANYA_API_KEY));
            JsonHttpClient.Response response = jsonHttpClient.post(url, FrontLineWorkerContractMapper.mapFrom(frontLineWorker), requestHeaders);
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
