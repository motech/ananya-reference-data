package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.utils.TestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class LocationControllerTest {

    private LocationController locationController;
    @Mock
    private LocationService locationService;
    @Mock
    private CsvUploadRequest mockLocationsCsvRequest;

    @Before
    public void setUp() {
        locationController = new LocationController(locationService);
    }

    @Test
    public void shouldGetLocationMasterCsv() throws Exception {
        final Location validLocation = new Location("state", "d1", "b1", "p1", LocationStatus.VALID, null);
        String channel = "contact_center";
        when(locationService.getAllValidLocations()).thenReturn(new ArrayList<Location>() {{
            add(validLocation);
        }});

        LocationResponseList responses = locationController.getLocationMaster(channel);

        assertEquals(1, responses.size());
        assertLocationResponse(validLocation, responses.get(0));
        assertNotNull(responses.getFileName());
    }

    @Test
    public void shouldValidateChannel() throws Exception {
        String channel = "blah";
        mockMvc(locationController)
                .perform(get("/alllocations").param("channel", channel).accept(new MediaType("text", "csv", Charset.defaultCharset())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void shouldSyncAValidLocation() throws Exception {
        LocationRequest locationRequest = new LocationRequest("state", "district", "block", "panchayat");

        MvcResult result = mockMvc(locationController)
                .perform(post("/location")
                        .body(TestUtils.toJson(locationRequest).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(LocationStatus.NOT_VERIFIED.name(), locationRequest.getStatus());
        verify(locationService).createAndFetch(locationRequest);
        String responseString = result.getResponse().getContentAsString();
        BaseResponse actualResponse = TestUtils.fromJson(BaseResponse.class, responseString);
        assertEquals(BaseResponse.success("New location has been synchronized successfully."), actualResponse);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowValidationExceptionAndNotSyncForAnInvalidLocation(){
        locationController.syncLocation(new LocationRequest());

        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldReturnBadRequestForInvalidLocation() throws Exception {
        mockMvc(locationController)
                .perform(post("/location")
                .body(TestUtils.toJson(new LocationRequest()).getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private void assertLocationResponse(Location expectedLocation, LocationResponse response) {
        LocationResponse locationResponse = response;
        assertEquals(expectedLocation.getBlock(), locationResponse.getBlock());
        assertEquals(expectedLocation.getDistrict(), locationResponse.getDistrict());
        assertEquals(expectedLocation.getPanchayat(), locationResponse.getPanchayat());
    }
}
