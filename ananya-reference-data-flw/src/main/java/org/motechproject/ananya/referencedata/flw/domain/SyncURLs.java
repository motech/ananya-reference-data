package org.motechproject.ananya.referencedata.flw.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class SyncURLs {
    private static final String FLW_SYNC_ENDPOINT_URL_PROPERTY = "front.line.worker.create.url";
    private static final  String LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX = "location.sync.url.";

    private String flwSyncEndpointUrl;
    private List<String> locationSyncEndpointUrls;

    @Autowired
    public SyncURLs(@Qualifier("clientServicesProperties") Properties clientServicesProperties) {
        flwSyncEndpointUrl = clientServicesProperties.getProperty(FLW_SYNC_ENDPOINT_URL_PROPERTY);
        parseLocationSyncUrls(clientServicesProperties);
    }

    public String getFlwSyncEndpointUrl() {
        return flwSyncEndpointUrl;
    }

    public List<String> getLocationSyncEndpointUrls() {
        return locationSyncEndpointUrls;
    }

    private void parseLocationSyncUrls(Properties clientServicesProperties) {
        locationSyncEndpointUrls = new ArrayList<>();
        for(String propertyName: clientServicesProperties.stringPropertyNames()){
            if(propertyName.startsWith(LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX)) {
                locationSyncEndpointUrls.add(clientServicesProperties.getProperty(propertyName));
            }
        }
    }
}