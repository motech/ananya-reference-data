package org.motechproject.ananya.referencedata.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.service.LocationService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationControllerTest {

    @Mock
    private LocationService locationService;

    private LocationController locationController;

    @Before
    public void setUp() {
        initMocks(this);
        locationController = new LocationController(locationService);
    }

    @Test
    public void shouldAddNewLocation() {
        LocationRequest locationRequest = new LocationRequest();
        LocationCreationResponse response = new LocationCreationResponse(new Location());
        when(locationService.add(locationRequest)).thenReturn(response);

        LocationCreationResponse actualResponse = locationController.create(locationRequest);
        assertEquals(response, actualResponse);
    }
}
