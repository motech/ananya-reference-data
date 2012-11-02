package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.LocationImportRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationImporterTest {

    @Mock
    private LocationImportService locationImportService;
    @Mock
    private LocationImportValidator locationImportValidator;
    @Captor
    private ArgumentCaptor<List<LocationImportRequest>> captor;
    private LocationImporter locationImporter;
    private List<LocationImportRequest> locationImportRequests;

    @Before
    public void setUp() {
        initMocks(this);
        locationImporter = new LocationImporter(locationImportService, locationImportValidator);
        locationImportRequests = new ArrayList<>();
    }

    @Test
    public void shouldValidateLocationRequests() {
        final LocationImportRequest locationImportRequest = new LocationImportRequest("D1", "B1", "P1", LocationStatus.VALID.name());
        locationImportRequests.add(locationImportRequest);
        ArrayList<Object> locationRequests = new ArrayList<Object>(){{
            add(locationImportRequest);
        }};
        when(locationImportValidator.validate(locationImportRequest, locationImportRequests)).
                thenReturn(new LocationValidationResponse());

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfLocationDoesNotHaveAllTheDetails() {
        ArrayList<Object> locationRequests = new ArrayList<>();
        LocationImportRequest locationImportRequest = new LocationImportRequest("D1", "B1", null, "VALID");
        locationRequests.add(locationImportRequest);
        locationImportRequests.add(locationImportRequest);
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forBlankFieldsInLocation();
        when(locationImportValidator.validate(locationImportRequest, locationImportRequests)).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"null\",\"[Blank district, block or panchayat]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateLocations() {
        LocationImportRequest locationImportRequest = new LocationImportRequest("D1", "B1", "P1", "VALID");
        ArrayList<Object> locationRequests = new ArrayList<>();
        locationRequests.add(locationImportRequest);
        locationImportRequests.add(locationImportRequest);
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forDuplicateLocation();
        when(locationImportValidator.validate(locationImportRequest, locationImportRequests)).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"P1\",\"[Location already present]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldSaveLocation() {
        ArrayList<Object> locationRequests = new ArrayList<>();
        locationRequests.add(new LocationImportRequest("D1", "B1", "P1", "VALID"));

        locationImporter.postData(locationRequests);

        verify(locationImportService).addAllWithoutValidations(captor.capture());
        List<LocationImportRequest> locationRequestsToSave = captor.getValue();
        assertEquals(1, locationRequestsToSave.size());
        assertEquals("D1", locationRequestsToSave.get(0).getDistrict());
        assertEquals("B1", locationRequestsToSave.get(0).getBlock());
        assertEquals("P1", locationRequestsToSave.get(0).getPanchayat());
        assertEquals("VALID", locationRequestsToSave.get(0).getStatus());
    }
}