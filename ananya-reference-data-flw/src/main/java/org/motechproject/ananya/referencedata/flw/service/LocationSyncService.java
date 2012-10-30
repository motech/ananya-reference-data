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
import java.util.Properties;

@Service
public class LocationSyncService {
    private HttpClientService httpClientService;
    private Properties clientServicesProperties;
    Logger logger = Logger.getLogger(LocationSyncService.class);

    @Autowired
    public LocationSyncService(HttpClientService httpClientService, Properties clientServicesProperties) {
        this.httpClientService = httpClientService;
        this.clientServicesProperties = clientServicesProperties;
    }

    public void sync(List<Location> locations) {
        for (Location location : locations) {
            logger.info("Raising event to sync for location: " + location.toString());
            LocationSyncRequest locationSyncRequest = LocationSyncRequestMapper.map(location);
            httpClientService.post((String) clientServicesProperties.get(SyncURLs.KEY_LOCATION_SYNC_FLW_URL), locationSyncRequest);
            httpClientService.post((String) clientServicesProperties.get(SyncURLs.KEY_LOCATION_SYNC_KILKARI_URL), locationSyncRequest);
        }
    }
}
