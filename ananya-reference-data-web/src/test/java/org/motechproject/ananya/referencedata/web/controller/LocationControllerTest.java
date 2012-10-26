package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.validator.WebRequestValidator;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    private LocationController locationController;
    @Mock
    private LocationService locationService;
    @Mock
    private WebRequestValidator webRequestValidator;

    @Before
    public void setUp() {
        locationController = new LocationController(locationService, webRequestValidator);
    }

    @Test
    public void shouldGetLocationMasterCsv() throws Exception {
        final Location validLocation = new Location("d1", "b1", "p1", "valid");
        String channel = "contact_center";
        when(webRequestValidator.validateChannel(channel)).thenReturn(new Errors());
        when(locationService.getAllValidLocations()).thenReturn(new ArrayList<Location>() {{
            add(validLocation);
        }});

        LocationResponseList responses = locationController.getLocationMaster("contact_center");

        assertEquals(1, responses.size());
        LocationResponse locationResponse = responses.get(0);
        assertEquals(validLocation.getBlock(), locationResponse.getBlock());
        assertEquals(validLocation.getDistrict(), locationResponse.getDistrict());
        assertEquals(validLocation.getPanchayat(), locationResponse.getPanchayat());
    }

    @Test
    public void shouldValidateChannel() throws Exception {
        String channel = "blah";
        Errors errors = new Errors();
        errors.add("some error");
        when(webRequestValidator.validateChannel(channel)).thenReturn(errors);

        mockMvc(locationController)
                .perform(get("/alllocations").param("channel", channel).accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
