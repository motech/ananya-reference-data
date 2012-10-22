package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.service.LocationService;

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
        LocationCreationResponse response = new LocationCreationResponse();
        when(locationService.add(locationRequest)).thenReturn(response);

        LocationCreationResponse actualResponse = locationController.create(locationRequest);
        assertEquals(response, actualResponse);
    }
}
