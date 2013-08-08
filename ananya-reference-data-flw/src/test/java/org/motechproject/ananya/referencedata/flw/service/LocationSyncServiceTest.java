package org.motechproject.ananya.referencedata.flw.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpoint;
import org.motechproject.ananya.referencedata.flw.domain.SyncEndpointService;
import org.motechproject.ananya.referencedata.flw.service.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.request.LocationSyncRequest;
import org.motechproject.http.client.service.HttpClientService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationSyncServiceTest {
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private SyncEndpointService syncEndpointService;

    @Test
    public void shouldSyncAllLocations() {
        LocationSyncService locationSyncService = new LocationSyncService(httpClientService, syncEndpointService);
        final Location locationToBeSynced = new Location("state", "D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null);
        DateTime dateTime = DateTime.now();
        locationToBeSynced.setLastModified(dateTime);
        String flwApiKeyName = "flwApiKeyName";
        String flwApiKeyValue = "flwApiKeyValue";
        SyncEndpoint flwSyncEndpoint = new SyncEndpoint("flwUrl", flwApiKeyName, flwApiKeyValue);
        String kilkariApiKeyName = "kilkariApiKeyName";
        String kilkariApiKeyValue = "kilkariApiKey";
        SyncEndpoint kilkariSyncEndpoint = new SyncEndpoint("kilkariUrl", kilkariApiKeyName, kilkariApiKeyValue);
        when(syncEndpointService.getLocationSyncEndpoints()).thenReturn(Arrays.asList(flwSyncEndpoint, kilkariSyncEndpoint));
        Map<String, String> headers1 = new HashMap<>();
        headers1.put(flwApiKeyName, flwApiKeyValue);
        Map<String, String> headers2 = new HashMap<>();
        headers2.put(kilkariApiKeyName, kilkariApiKeyValue);

        locationSyncService.sync(locationToBeSynced);

        LocationRequest actualLocation = new LocationRequest(locationToBeSynced.getState(), locationToBeSynced.getDistrict(), locationToBeSynced.getBlock(), locationToBeSynced.getPanchayat());
        LocationSyncRequest expectedLocationSyncRequest = new LocationSyncRequest(actualLocation, actualLocation, locationToBeSynced.getStatus(), locationToBeSynced.getLastModified());
        verify(httpClientService).post(flwSyncEndpoint.getUrl(), expectedLocationSyncRequest, headers1);
        verify(httpClientService).post(kilkariSyncEndpoint.getUrl(), expectedLocationSyncRequest, headers2);
    }
}
