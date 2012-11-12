package org.motechproject.ananya.referencedata.flw.service;

import org.apache.log4j.Logger;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.SyncURLs;
import org.motechproject.ananya.referencedata.flw.mapper.LocationSyncRequestMapper;
import org.motechproject.ananya.referencedata.flw.service.request.LocationSyncRequest;
import org.motechproject.http.client.service.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationSyncService {
    private HttpClientService httpClientService;
    private SyncURLs syncURLs;

    private Logger logger = Logger.getLogger(LocationSyncService.class);

    @Autowired
    public LocationSyncService(HttpClientService httpClientService, SyncURLs syncURLs) {
        this.httpClientService = httpClientService;
        this.syncURLs = syncURLs;
    }

    public void sync(Location location) {
        logger.info("Raising event to sync for location: " + location.toString());
        LocationSyncRequest locationSyncRequest = LocationSyncRequestMapper.map(location);
        List<String> locationSyncEndpointUrls = syncURLs.getLocationSyncEndpointUrls();
        for (String locationEndPointUrl : locationSyncEndpointUrls)
            httpClientService.post(locationEndPointUrl, locationSyncRequest);
    }
}