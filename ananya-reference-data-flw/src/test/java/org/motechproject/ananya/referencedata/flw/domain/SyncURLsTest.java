package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SyncURLsTest {

    @Test
    public void shouldReturnFlwSyncEndpointUrl() {
        Properties clientServicesProperties = new Properties();
        clientServicesProperties.setProperty("front.line.worker.sync.url", "myUrl");
        SyncURLs syncURLs = new SyncURLs(clientServicesProperties);
        assertEquals("myUrl", syncURLs.getFlwSyncEndpointUrl());
    }

    @Test
    public void shouldReturnLocationSyncEndpointUrls() {
        Properties clientServicesProperties = new Properties();
        clientServicesProperties.setProperty("location.sync.url.one", "myUrl1");
        clientServicesProperties.setProperty("location.sync.url.two", "myUrl2");
        clientServicesProperties.setProperty("location.sync.url.", "myUrl3");
        clientServicesProperties.setProperty("location.sync.url", "myUrl4");
        clientServicesProperties.setProperty("location.sync.url.three.four", "myUrl5");
        clientServicesProperties.setProperty("location.sync.five", "myUrl6");

        SyncURLs syncURLs = new SyncURLs(clientServicesProperties);
        List<String> urls = syncURLs.getLocationSyncEndpointUrls();
        assertEquals(4, urls.size());
        assertTrue(urls.contains("myUrl1"));
        assertTrue(urls.contains("myUrl2"));
        assertTrue(urls.contains("myUrl3"));
        assertTrue(urls.contains("myUrl5"));
    }
}