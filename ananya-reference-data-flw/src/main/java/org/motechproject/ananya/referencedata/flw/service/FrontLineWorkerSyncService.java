package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpoint;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpointService;
import org.motechproject.ananya.referencedata.flw.mapper.FrontLineWorkerSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.response.SyncResponse;
import org.motechproject.ananya.referencedata.flw.response.SyncResponseFlw;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FrontLineWorkerSyncService {
   
    private SyncEndpointService syncEndpointService;
    private AllFrontLineWorkers allFrontLineWorkers;
    Logger logger = Logger.getLogger(FrontLineWorkerSyncService.class);

    @Autowired
    public FrontLineWorkerSyncService(SyncEndpointService syncEndpointService, AllFrontLineWorkers allFrontLineWorkers) {
        this.syncEndpointService = syncEndpointService;
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public List<SyncResponseFlw> sync(List<FrontLineWorker> frontLineWorkerList) {
    	List<SyncResponseFlw> responseList = new ArrayList<SyncResponseFlw>();
        for (FrontLineWorker frontLineWorker : frontLineWorkerList) {
            logger.info("Raising event to sync for flw: " + frontLineWorker.toString());
            if (frontLineWorker.hasBeenVerified() || dbHasOnlyOneFlwByMsisdn(frontLineWorker)) {
                List<SyncEndpoint> flwSyncEndpoints = syncEndpointService.getFlwSyncEndpoints();
                for (SyncEndpoint flwSyncEndpoint : flwSyncEndpoints) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put(flwSyncEndpoint.getApiKeyName(), flwSyncEndpoint.getApiKeyValue());
                    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
                    factory.setConnectTimeout(90000);
                    factory.setReadTimeout(90000);
                    RestTemplate restTemplate = new RestTemplate(factory);      
                    Object requestdata = FrontLineWorkerSyncRequestMapper.mapFrom(frontLineWorker);
                    HttpEntity<Object> entity = new HttpEntity<Object>(requestdata, createHttpHeaders(headers));
                    try { 
                    	logger.info("Posting data with url "+flwSyncEndpoint.getUrl()
                        		+ " API Key "
                        		+flwSyncEndpoint.getApiKeyName()
                        		+":"
                        		+flwSyncEndpoint.getApiKeyValue()
                        		+" Data "+
                        		requestdata);
                    	restTemplate.postForLocation(flwSyncEndpoint.getUrl(),entity);
                    	 logger.info("Posted data with url "+flwSyncEndpoint.getUrl()
                          		+ " API Key "
                          		+flwSyncEndpoint.getApiKeyName()
                          		+":"
                          		+flwSyncEndpoint.getApiKeyValue()
                          		+" Data "+
                          		requestdata);
                    	responseList.add(new SyncResponseFlw(true));
                    } catch (Exception e) {
                    	logger.info("Posting data with url "+flwSyncEndpoint.getUrl()
                         		+ " API Key "
                         		+flwSyncEndpoint.getApiKeyName()
                         		+":"
                         		+flwSyncEndpoint.getApiKeyValue()
                         		+" Data "
                         		+ requestdata
                         		+" failed due to "
                         		+ e.getMessage()
                         		);	
                    	responseList.add(new SyncResponseFlw(false));
                    }
                   
                    
             }
          }
        }
		return responseList;
    }
    
    private HttpHeaders createHttpHeaders(Map<String, String> headers) {
        if(headers == null) return null;
        HttpHeaders httpHeaders = new HttpHeaders();
        for(String param : headers.keySet()){
            httpHeaders.add(param, headers.get(param));
        }
        return httpHeaders;
    }

    private boolean dbHasOnlyOneFlwByMsisdn(FrontLineWorker frontLineWorker) {
        List<FrontLineWorker> flwsByMsisdn = frontLineWorker.msisdnChange()
                ? allFrontLineWorkers.getByMsisdn(frontLineWorker.getNewMsisdn().msisdn())
                : allFrontLineWorkers.getByMsisdn(frontLineWorker.getMsisdn());
        return flwsByMsisdn.size() == 1;
    }
}