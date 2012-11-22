package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpoint;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpointService;
import org.motechproject.ananya.referencedata.flw.mapper.LocationSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.service.request.LocationSyncRequest;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationSyncService {
    private HttpClientService httpClientService;
    private SyncEndpointService syncEndpointService;

    private Logger logger = Logger.getLogger(LocationSyncService.class);

    @Autowired
    public LocationSyncService(HttpClientService httpClientService, SyncEndpointService syncEndpointService) {
        this.httpClientService = httpClientService;
        this.syncEndpointService = syncEndpointService;
    }

    public void sync(Location location) {
        logger.info("Raising event to sync for location: " + location.toString());
        LocationSyncRequest locationSyncRequest = LocationSyncRequestMapper.map(location);
        List<SyncEndpoint> locationSyncEndpointUrls = syncEndpointService.getLocationSyncEndpoints();
        for (SyncEndpoint locationEndPointUrl : locationSyncEndpointUrls) {
            Map<String, String> headers = new HashMap<>();
            headers.put(SyncEndpoint.API_KEY, locationEndPointUrl.getApiKey());
            httpClientService.post(locationEndPointUrl.getUrl(), locationSyncRequest, headers);
        }
    }
}