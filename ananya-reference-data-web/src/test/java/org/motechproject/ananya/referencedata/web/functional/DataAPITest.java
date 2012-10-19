package org.motechproject.ananya.referencedata.web.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.flw.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.flw.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.flw.service.JsonHttpClient;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerWebRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

//TODO
@Ignore
public class DataAPITest extends SpringIntegrationTest {

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
    public void shouldCreateNewFLW() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClientForFLW = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientForFLW.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForInvalidMsisdn() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldCreateNewFLWForInvalidDesignation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForUnavailableLocation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("Invalid location", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldUpdateAnExistingFLW() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient();
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        jsonHttpClientFLW.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class);
        
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientFLW.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldAddFLWWhenFLWDoesNotExistForUpdate() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient();
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/ananya-reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);

        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientFLW.post("http://localhost:9979/ananya-reference-data/flw", new FrontLineWorkerWebRequest("guid", "INVALID","Timepass"), FrontLineWorkerResponse.class).body;

        assertEquals("FLW created/updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldHandleExceptionAndSetErrorResponseStatus() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();

        Map<String, String> headers = new HashMap<String, String>();
        ExceptionResponse exceptionResponse = (ExceptionResponse) jsonHttpClient.post("http://localhost:9979/ananya-reference-data/location", "\"foo\":\"bar\"", ExceptionResponse.class, headers).body;

        assertNotNull(exceptionResponse);
    }
}