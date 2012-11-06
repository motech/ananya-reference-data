package org.motechproject.ananya.referencedata.flw.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.domain.SyncURLs;
import org.motechproject.ananya.referencedata.flw.service.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.request.LocationSyncRequest;
import org.motechproject.http.client.service.HttpClientService;

import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationSyncServiceTest {
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private Properties clientServiceProperties;

    @Test
    public void shouldSyncAllLocations() {
        LocationSyncService locationSyncService = new LocationSyncService(httpClientService, clientServiceProperties);
        final Location locationToBeSynced = new Location("D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null);
        DateTime dateTime = DateTime.now();
        locationToBeSynced.setLastModified(dateTime);
        String flwUrl = "flwUrl";
        String kilkariUrl = "kilkariUrl";
        when(clientServiceProperties.getProperty(SyncURLs.KEY_LOCATION_SYNC_FLW_URL)).thenReturn(flwUrl);
        when(clientServiceProperties.getProperty(SyncURLs.KEY_LOCATION_SYNC_KILKARI_URL)).thenReturn(kilkariUrl);

        locationSyncService.sync(locationToBeSynced);

        LocationRequest actualLocation = new LocationRequest(locationToBeSynced.getDistrict(), locationToBeSynced.getBlock(), locationToBeSynced.getPanchayat());
        LocationSyncRequest expectedLocationSyncRequest = new LocationSyncRequest(actualLocation, actualLocation, locationToBeSynced.getStatus(), locationToBeSynced.getLastModified());
        verify(httpClientService).post(flwUrl, expectedLocationSyncRequest);
        verify(httpClientService).post(kilkariUrl, expectedLocationSyncRequest);
    }
}
