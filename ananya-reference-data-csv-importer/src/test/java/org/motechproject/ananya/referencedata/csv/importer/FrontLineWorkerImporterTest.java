package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.FrontLineWorkerRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.JsonHttpClient;
import org.motechproject.ananya.referencedata.flw.service.LocationService;
import org.motechproject.importer.domain.ValidationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.*;
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
        frontLineWorkerImporter = new FrontLineWorkerImporter(frontLineWorkerService, locationService);
    }

    @Test
    public void shouldValidateFLWRequests() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1");
        when(locationService.getFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("msisdn,name,designation,district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfFLWDoesNotHaveAllTheDetails() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        Location location = new Location("D1", "B1", "P1");
        when(locationService.getFor("D1", "B1", "P1")).thenReturn(location);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1asdf67890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"1asdf67890\",\"name\",\"ANM\",\"D1\",\"B1\",\"P1\",\"[Invalid msisdn]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldSaveFLW() throws IOException {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        String msisdn = "1234567890";
        frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        frontLineWorkerImporter.postData(frontLineWorkerRequests);

        verify(frontLineWorkerService).addAllWithoutValidations(frontLineWorkerRequests);
    }
}
