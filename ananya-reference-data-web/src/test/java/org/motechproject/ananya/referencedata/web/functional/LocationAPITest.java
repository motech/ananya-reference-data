package org.motechproject.ananya.referencedata.web.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.service.JsonHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LocationAPITest extends SpringIntegrationTest {

    @Before
    @After
    public void tearDown() {
        template.deleteAll(template.loadAll(FrontLineWorker.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldNotCreateLocationWhenAnyOfDetailIsNullOrMissing() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", null), LocationCreationResponse.class).body;

        assertEquals("Blank district, block or panchayat", locationResponse.getMessage());
    }

    @Test
    public void shouldCreateLocationWhenAllDetailsAreValid() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class).body;

        assertEquals("Location created successfully", locationResponse.getMessage());
    }

    @Test
    public void shouldNotCreateLocationWhenAlreadyPresent() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class).body;

        assertEquals("Location already present", locationResponse.getMessage());
    }

    @Test
    public void shouldHandleExceptionAndSetErrorResponseStatus() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type", "application/json");
        headers.put("Accept", "application/json");
        BaseResponse response = (BaseResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", "\"foo\":\"bar\"", BaseResponse.class, headers).body;

        assertNotNull(response);
    }
}