package org.motechproject.ananya.referencedata.flw.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class SyncURLs {
    private static final String FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX = "front.line.worker.sync.url";
    private static final  String LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX = "location.sync.url.";

    private List<String> flwSyncEndpointUrls;
    private List<String> locationSyncEndpointUrls;

    @Autowired
    public SyncURLs(@Qualifier("clientServicesProperties") Properties clientServicesProperties) {
        parseFLWSyncUrls(clientServicesProperties);
        parseLocationSyncUrls(clientServicesProperties);
    }

    public List<String> getFlwSyncEndpointUrls() {
        return flwSyncEndpointUrls;
    }

    public List<String> getLocationSyncEndpointUrls() {
        return locationSyncEndpointUrls;
    }

    private void parseFLWSyncUrls(Properties clientServicesProperties) {
        flwSyncEndpointUrls = new ArrayList<>();
        for(String propertyName: clientServicesProperties.stringPropertyNames()){
            if(propertyName.startsWith(FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX)) {
                flwSyncEndpointUrls.add(clientServicesProperties.getProperty(propertyName));
            }
        }
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