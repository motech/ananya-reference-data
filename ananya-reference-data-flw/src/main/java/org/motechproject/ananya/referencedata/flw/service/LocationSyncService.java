package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpoint;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpointService;
import org.motechproject.ananya.referencedata.flw.mapper.LocationSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.response.SyncResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationSyncService {

    private SyncEndpointService syncEndpointService;

    private Logger logger = Logger.getLogger(LocationSyncService.class);

    @Autowired
    public LocationSyncService(SyncEndpointService syncEndpointService) {     
        this.syncEndpointService = syncEndpointService;
    }

    public List<SyncResponse> sync(Location location) {
    	List<SyncResponse> responseList = new ArrayList<SyncResponse>();
        logger.info("Raising event to sync for location: " + location.toString());
        
       
        List<SyncEndpoint> locationSyncEndpointUrls = syncEndpointService.getLocationSyncEndpoints();
        for (SyncEndpoint locationEndPointUrl : locationSyncEndpointUrls) {
            Map<String, String> headers = new HashMap<>();
            headers.put(locationEndPointUrl.getApiKeyName(), locationEndPointUrl.getApiKeyValue());
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(90000);
            factory.setReadTimeout(90000);
            RestTemplate restTemplate = new RestTemplate(factory);
            Object requestdata = LocationSyncRequestMapper.map(location);
            HttpEntity<Object> entity = new HttpEntity<Object>(requestdata, createHttpHeaders(headers));
            try {  
                logger.info("Posting data with url "+locationEndPointUrl.getUrl()
                		+ " API Key "
                		+locationEndPointUrl.getApiKeyName()
                		+":"
                		+locationEndPointUrl.getApiKeyValue()
                		+" Data "+
                		requestdata);	
            	restTemplate.postForLocation(locationEndPointUrl.getUrl(), entity);
            	 logger.info("Posted data with url "+locationEndPointUrl.getUrl()
                 		+ " API Key "
                 		+locationEndPointUrl.getApiKeyName()
                 		+":"
                 		+locationEndPointUrl.getApiKeyValue()
                 		+" Data "+
                 		requestdata);	
            	responseList.add(new SyncResponse(location.getStatus().toString(),true));
            }
            catch(Exception e) {
            	 logger.info("Posting data with url "+locationEndPointUrl.getUrl()
                 		+ " API Key "
                 		+locationEndPointUrl.getApiKeyName()
                 		+":"
                 		+locationEndPointUrl.getApiKeyValue()
                 		+" Data "
                 		+ requestdata
                 		+" failed due to "
                 		+ e.getMessage()
                 		);	
            	responseList.add(new SyncResponse(location.getStatus().toString(),false));
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

}