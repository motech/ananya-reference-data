package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncURLs;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerSyncRequestMapper;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class FrontLineWorkerSyncService {
    private HttpClientService httpClientService;
    private Properties clientServicesProperties;
    Logger logger = Logger.getLogger(FrontLineWorkerSyncService.class);

    @Autowired
    public FrontLineWorkerSyncService(HttpClientService httpClientService, Properties clientServicesProperties) {
        this.httpClientService = httpClientService;
        this.clientServicesProperties = clientServicesProperties;
    }

    public void sync(List<FrontLineWorker> frontLineWorkerList) {
        for (FrontLineWorker frontLineWorker : frontLineWorkerList) {
            logger.info("Raising event to sync for flw: " + frontLineWorker.toString());
            httpClientService.post(clientServicesProperties.getProperty(SyncURLs.KEY_FRONT_LINE_WORKER_CREATE_URL), FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker));
        }
    }
}
