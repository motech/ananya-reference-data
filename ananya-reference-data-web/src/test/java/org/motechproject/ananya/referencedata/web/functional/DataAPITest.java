package org.motechproject.ananya.referencedata.web.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.response.FrontLineWorkerResponse;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", null), LocationCreationResponse.class);

        assertEquals("Blank district, block or panchayat", locationResponse.getMessage());
    }

    @Test
    public void shouldCreateLocationWhenAllDetailsAreValid() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);

        assertEquals("Location created successfully", locationResponse.getMessage());
    }

    @Test
    public void shouldNotCreateLocationWhenAlreadyPresent() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);

        assertEquals("Location already present", locationResponse.getMessage());
    }

    @Test
    public void shouldCreateNewFLW() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClientForFLW = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientForFLW.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);

        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForInvalidMsisdn() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999", "name", "ASHA", new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);

        assertEquals("Invalid msisdn", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldCreateNewFLWForInvalidDesignation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", null, new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);

        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForUnavailableLocation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        JsonHttpClient jsonHttpClient = new JsonHttpClient();
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "invalid_panchayat")), FrontLineWorkerResponse.class);

        assertEquals("Invalid location", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldUpdateAnExistingFLW() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient();
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);
        jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);
        
        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw?_method=PUT", new FrontLineWorkerRequest("9999888822", "new name", "ANM", new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);

        assertEquals("FLW updated successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldAddFLWWhenFLWDoesNotExistForUpdate() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient();
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient();
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"), LocationCreationResponse.class);

        FrontLineWorkerResponse frontLineWorkerResponse = (FrontLineWorkerResponse) jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw?_method=PUT", new FrontLineWorkerRequest("9999888822", "new name", "ANM", new LocationRequest("district", "block", "panchayat")), FrontLineWorkerResponse.class);

        assertEquals("FLW created successfully", frontLineWorkerResponse.getMessage());
    }

    @Test
    public void shouldHandleExceptionAndSetErrorResponseStatus() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient();

        ExceptionResponse exceptionResponse = (ExceptionResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", "\"foo\":\"bar\"", ExceptionResponse.class);

        assertNotNull(exceptionResponse);
    }
}