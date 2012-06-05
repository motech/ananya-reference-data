package org.motechproject.ananya.referencedata.web.functional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.response.ExceptionResponse;
import org.motechproject.ananya.referencedata.response.FLWResponse;
import org.motechproject.ananya.referencedata.response.LocationCreationResponse;

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
        JsonHttpClient jsonHttpClient = new JsonHttpClient(LocationCreationResponse.class);
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", null));

        assertEquals("Blank district, block or panchayat", locationResponse.getMessage());
    }

    @Test
    public void shouldCreateLocationWhenAllDetailsAreValid() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(LocationCreationResponse.class);
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));

        assertEquals("Location created successfully", locationResponse.getMessage());
    }

    @Test
    public void shouldNotCreateLocationWhenAlreadyPresent() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        LocationCreationResponse locationResponse = (LocationCreationResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));

        assertEquals("Location already present", locationResponse.getMessage());
    }

    @Test
    public void shouldCreateNewFLW() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        JsonHttpClient jsonHttpClientForFLW = new JsonHttpClient(FLWResponse.class);
        FLWResponse flwResponse = (FLWResponse) jsonHttpClientForFLW.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "panchayat")));

        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForInvalidMsisdn() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        JsonHttpClient jsonHttpClient = new JsonHttpClient(FLWResponse.class);
        FLWResponse flwResponse = (FLWResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999", "name", "ASHA", new LocationRequest("district", "block", "panchayat")));

        assertEquals("Invalid msisdn", flwResponse.getMessage());
    }

    @Test
    public void shouldCreateNewFLWForInvalidDesignation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        JsonHttpClient jsonHttpClient = new JsonHttpClient(FLWResponse.class);
        FLWResponse flwResponse = (FLWResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", null, new LocationRequest("district", "block", "panchayat")));

        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldNotCreateNewFLWForUnavailableLocation() throws IOException {
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        JsonHttpClient jsonHttpClient = new JsonHttpClient(FLWResponse.class);
        FLWResponse flwResponse = (FLWResponse) jsonHttpClient.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "invalid_panchayat")));

        assertEquals("Invalid location", flwResponse.getMessage());
    }

    @Test
    public void shouldUpdateAnExistingFLW() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient(FLWResponse.class);
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));
        jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw", new FrontLineWorkerRequest("9999888822", "name", "ASHA", new LocationRequest("district", "block", "panchayat")));
        
        FLWResponse flwResponse = (FLWResponse) jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw?_method=PUT", new FrontLineWorkerRequest("9999888822", "new name", "ANM", new LocationRequest("district", "block", "panchayat")));

        assertEquals("FLW updated successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldAddFLWWhenFLWDoesNotExistForUpdate() throws IOException {
        JsonHttpClient jsonHttpClientFLW = new JsonHttpClient(FLWResponse.class);
        JsonHttpClient jsonHttpClientForLocation = new JsonHttpClient(LocationCreationResponse.class);
        jsonHttpClientForLocation.post("http://localhost:9979/reference-data/location", new LocationRequest("district", "block", "panchayat"));

        FLWResponse flwResponse = (FLWResponse) jsonHttpClientFLW.post("http://localhost:9979/reference-data/flw?_method=PUT", new FrontLineWorkerRequest("9999888822", "new name", "ANM", new LocationRequest("district", "block", "panchayat")));

        assertEquals("FLW created successfully", flwResponse.getMessage());
    }

    @Test
    public void shouldHandleExceptionAndSetErrorResponseStatus() throws IOException {
        JsonHttpClient jsonHttpClient = new JsonHttpClient(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) jsonHttpClient.post("http://localhost:9979/reference-data/location", "\"foo\":\"bar\"");

        assertNotNull(exceptionResponse);
    }
}