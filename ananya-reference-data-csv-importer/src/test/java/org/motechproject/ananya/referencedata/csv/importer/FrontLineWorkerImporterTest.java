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
import org.motechproject.ananya.referencedata.service.LocationService;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerImporterTest {

    @Mock
    private LocationService locationService;
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
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
        assertEquals("1asdf67890,name,ANM,D1,B1,P1,[Invalid msisdn]", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateFLWs() {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));
        frontLineWorkerRequests.add(new FrontLineWorkerRequest("1234567890", "anotherName", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(3, validationResponse.getErrors().size());
        assertEquals("1234567890,name,ANM,D1,B1,P1,[Found duplicate FLW with the same MSISDN]", validationResponse.getErrors().get(1).getMessage());
        assertEquals("1234567890,anotherName,ANM,D1,B1,P1,[Found duplicate FLW with the same MSISDN]", validationResponse.getErrors().get(2).getMessage());
    }

    @Test
    public void shouldSaveFLW() {
        ArrayList<Object> frontLineWorkerRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        String msisdn = "1234567890";
        frontLineWorkerRequests.add(new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest("D1", "B1", "P1")));

        frontLineWorkerImporter.postData(frontLineWorkerRequests);

        verify(frontLineWorkerService).addAllWithoutValidations(captor.capture());
        List<FrontLineWorkerRequest> flwRequests = captor.getValue();
        assertEquals(1, flwRequests.size());
        assertEquals(msisdn, flwRequests.get(0).getMsisdn());
    }
}
