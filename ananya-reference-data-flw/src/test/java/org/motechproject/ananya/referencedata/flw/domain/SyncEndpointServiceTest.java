package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class SyncEndpointServiceTest {

    @Test
    public void shouldReturnFlwSyncEndpointUrl() {
        Properties clientServicesProperties = new Properties();
        Properties apiKeysProperties = new Properties();
        clientServicesProperties.setProperty("front.line.worker.sync.url.flw", "myUrl");
        apiKeysProperties.setProperty("api.key.name.flw", "apiKey");
        apiKeysProperties.setProperty("api.key.value.flw", "1234");
        SyncEndpointService syncEndpointService = new SyncEndpointService(clientServicesProperties, apiKeysProperties);

        List<SyncEndpoint> flwSyncEndpointUrls = syncEndpointService.getFlwSyncEndpoints();

        assertEquals(1, flwSyncEndpointUrls.size());
        assertEquals("myUrl", flwSyncEndpointUrls.get(0).getUrl());
        assertEquals("apiKey", flwSyncEndpointUrls.get(0).getApiKeyName());
        assertEquals("1234", flwSyncEndpointUrls.get(0).getApiKeyValue());
    }

    @Test
    public void shouldReturnLocationSyncEndpointUrls() {
        Properties clientServicesProperties = new Properties();
        Properties apiKeysProperties = new Properties();
        clientServicesProperties.setProperty("location.sync.url.one", "myUrl1");
        apiKeysProperties.setProperty("api.key.name.one", "apiKey");
        apiKeysProperties.setProperty("api.key.value.one", "1234");
        SyncEndpointService syncEndpointService = new SyncEndpointService(clientServicesProperties, apiKeysProperties);

        List<SyncEndpoint> urls = syncEndpointService.getLocationSyncEndpoints();

        assertEquals(1, urls.size());
        assertEquals("myUrl1", urls.get(0).getUrl());
        assertEquals("apiKey", urls.get(0).getApiKeyName());
        assertEquals("1234", urls.get(0).getApiKeyValue());
    }
}