package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class SyncEndpointService {
    private static final String FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX = "front.line.worker.sync.url.";
    private static final String LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX = "location.sync.url.";
    private static final String API_KEY_NAME_PREFIX = "api.key.name.";
    private static final String API_KEY_VALUE_PREFIX = "api.key.value.";

    private List<SyncEndpoint> flwSyncEndpoints;
    private List<SyncEndpoint> locationSyncEndpoints;

    @Autowired
    public SyncEndpointService(@Qualifier("clientServicesProperties") Properties clientServicesProperties, @Qualifier("apiKeysProperties") Properties apiKeysProperties) {
        parseFLWSyncUrls(clientServicesProperties, apiKeysProperties);
        parseLocationSyncUrls(clientServicesProperties, apiKeysProperties);
    }

    public List<SyncEndpoint> getFlwSyncEndpoints() {
        return flwSyncEndpoints;
    }

    public List<SyncEndpoint> getLocationSyncEndpoints() {
        return locationSyncEndpoints;
    }

    private void parseFLWSyncUrls(Properties clientServicesProperties, Properties apiKeysProperties) {
        flwSyncEndpoints = new ArrayList<>();
        for (String propertyName : clientServicesProperties.stringPropertyNames()) {
            if (propertyName.startsWith(FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX)) {
                String url = clientServicesProperties.getProperty(propertyName);
                String apiKeyName = apiKeysProperties.getProperty(getApiPropertyKeyName(propertyName, FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX));
                String apiKeyValue = apiKeysProperties.getProperty(getApiPropertyKeyValue(propertyName, FLW_SYNC_ENDPOINT_URL_PROPERTY_PREFIX));
                flwSyncEndpoints.add(new SyncEndpoint(url, apiKeyName, apiKeyValue));
            }
        }
    }

    private void parseLocationSyncUrls(Properties clientServicesProperties, Properties apiKeysProperties) {
        locationSyncEndpoints = new ArrayList<>();
        for (String propertyName : clientServicesProperties.stringPropertyNames()) {
            if (propertyName.startsWith(LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX)) {
                String url = clientServicesProperties.getProperty(propertyName);
                String apiKeyName = apiKeysProperties.getProperty(getApiPropertyKeyName(propertyName, LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX));
                String apiKeyValue = apiKeysProperties.getProperty(getApiPropertyKeyValue(propertyName, LOCATION_SYNC_ENDPOINT_URL_PROPERTY_PREFIX));
                locationSyncEndpoints.add(new SyncEndpoint(url, apiKeyName, apiKeyValue));
            }
        }
    }

    private String getApiPropertyKeyValue(String propertyName, String endPointPrefix) {
        return API_KEY_VALUE_PREFIX + StringUtils.remove(propertyName, endPointPrefix);
    }

    private String getApiPropertyKeyName(String propertyName, String endPointPrefix) {
        return API_KEY_NAME_PREFIX + StringUtils.remove(propertyName, endPointPrefix);
    }
}