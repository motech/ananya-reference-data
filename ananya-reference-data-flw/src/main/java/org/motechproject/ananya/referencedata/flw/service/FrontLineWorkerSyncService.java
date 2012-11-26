package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpoint;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpointService;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FrontLineWorkerSyncService {
    private HttpClientService httpClientService;
    private SyncEndpointService syncEndpointService;
    private AllFrontLineWorkers allFrontLineWorkers;
    Logger logger = Logger.getLogger(FrontLineWorkerSyncService.class);

    @Autowired
    public FrontLineWorkerSyncService(HttpClientService httpClientService, SyncEndpointService syncEndpointService, AllFrontLineWorkers allFrontLineWorkers) {
        this.httpClientService = httpClientService;
        this.syncEndpointService = syncEndpointService;
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public void sync(List<FrontLineWorker> frontLineWorkerList) {
        for (FrontLineWorker frontLineWorker : frontLineWorkerList) {
            logger.info("Raising event to sync for flw: " + frontLineWorker.toString());
            if (frontLineWorker.hasBeenVerified() || allFrontLineWorkers.getByMsisdn(frontLineWorker.getMsisdn()).size() == 1) {
                List<SyncEndpoint> flwSyncEndpoints = syncEndpointService.getFlwSyncEndpoints();
                for (SyncEndpoint flwSyncEndpoint : flwSyncEndpoints) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(flwSyncEndpoint.getApiKeyName(), flwSyncEndpoint.getApiKeyValue());
                    httpClientService.post(flwSyncEndpoint.getUrl(), FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker), headers);
                }
            }
        }
    }
}