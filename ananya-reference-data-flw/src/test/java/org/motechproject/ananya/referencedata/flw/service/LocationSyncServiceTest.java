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

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationSyncServiceTest {
    @Mock
    private HttpClientService httpClientService;
    @Mock
    private SyncURLs syncURLs;

    @Test
    public void shouldSyncAllLocations() {
        LocationSyncService locationSyncService = new LocationSyncService(httpClientService, syncURLs);
        final Location locationToBeSynced = new Location("D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null);
        DateTime dateTime = DateTime.now();
        locationToBeSynced.setLastModified(dateTime);
        String flwUrl = "flwUrl";
        String kilkariUrl = "kilkariUrl";
        when(syncURLs.getLocationSyncEndpointUrls()).thenReturn(Arrays.asList(flwUrl,kilkariUrl));

        locationSyncService.sync(locationToBeSynced);

        LocationRequest actualLocation = new LocationRequest(locationToBeSynced.getDistrict(), locationToBeSynced.getBlock(), locationToBeSynced.getPanchayat());
        LocationSyncRequest expectedLocationSyncRequest = new LocationSyncRequest(actualLocation, actualLocation, locationToBeSynced.getStatus(), locationToBeSynced.getLastModified());
        verify(httpClientService).post(flwUrl, expectedLocationSyncRequest);
        verify(httpClientService).post(kilkariUrl, expectedLocationSyncRequest);
    }
}
