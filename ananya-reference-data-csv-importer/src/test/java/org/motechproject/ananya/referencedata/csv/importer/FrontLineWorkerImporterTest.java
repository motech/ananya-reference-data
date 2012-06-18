package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.domain.Designation;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.request.LocationRequest;
import org.motechproject.ananya.referencedata.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.service.JsonHttpClient;
import org.motechproject.ananya.referencedata.service.LocationService;
import org.motechproject.importer.domain.ValidationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerImporterTest {

    @Mock
    private LocationService locationService;
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private JsonHttpClient jsonHttpClient;
    @Mock
    private Properties clientServicesProperties;
    @Captor
    private ArgumentCaptor<List<FrontLineWorkerRequest>> captor;
    private FrontLineWorkerImporter frontLineWorkerImporter;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImporter = new FrontLineWorkerImporter(frontLineWorkerService, locationService, jsonHttpClient, clientServicesProperties);
    }

    @Test
    public void shouldValidateFLWRequests() {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("mssidn,name,desigantion,district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfFLWDoesNotHaveAllTheDetails() {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1asdf67890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"1asdf67890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"[Invalid msisdn]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldSaveFLW() throws IOException {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        String bulkUrl = "http://localhost:9979/ananya-reference-data/flw/bulk_import";
        String msisdn = "1234567890";
        frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));
        when(clientServicesProperties.get("front.line.worker.bulk.import.url")).thenReturn(bulkUrl);
        when(locationService.getAll()).thenReturn(locations);
        when(jsonHttpClient.post(any(String.class), any())).thenReturn(new JsonHttpClient.Response(200, null));

        frontLineWorkerImporter.postData(frontLineWorkerRequests);

        verify(jsonHttpClient).post(bulkUrl, frontLineWorkerRequests);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTheReturnCodeIsAnythingOtherThan200() throws IOException {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        String bulkUrl = "http://localhost:9979/ananya-reference-data/flw/bulk_import";
        String msisdn = "1234567890";
        frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));
        when(clientServicesProperties.get("front.line.worker.bulk.import.url")).thenReturn(bulkUrl);
        when(locationService.getAll()).thenReturn(locations);
        when(jsonHttpClient.post(any(String.class), any())).thenReturn(new JsonHttpClient.Response(500, null));

        frontLineWorkerImporter.postData(frontLineWorkerRequests);
    }
}
