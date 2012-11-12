package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.csv.utils.LocationImportCSVRequestBuilder;
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
    private ArgumentCaptor<List<LocationImportCSVRequest>> captor;
    private LocationImporter locationImporter;
    private List<LocationImportCSVRequest> locationImportCSVRequests;

    @Before
    public void setUp() {
        initMocks(this);
        locationImporter = new LocationImporter(locationImportService, locationImportValidator);
        locationImportCSVRequests = new ArrayList<>();
    }

    @Test
    public void shouldValidateLocationRequests() {
        final LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.VALID.getDescription());
        locationImportCSVRequests.add(locationImportCSVRequest);
        ArrayList<Object> locationRequests = new ArrayList<Object>(){{
            add(locationImportCSVRequest);
        }};
        when(locationImportValidator.validate(locationImportCSVRequest, locationImportCSVRequests)).
                thenReturn(new LocationValidationResponse());

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfLocationDoesNotHaveAllTheDetails() {
        ArrayList<Object> locationRequests = new ArrayList<>();
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", null, "VALID");
        locationRequests.add(locationImportCSVRequest);
        locationImportCSVRequests.add(locationImportCSVRequest);
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forBlankFieldsInLocation();
        when(locationImportValidator.validate(locationImportCSVRequest, locationImportCSVRequests)).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"null\",\"[Blank district, block or panchayat]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateLocations() {
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", "P1", "VALID");
        ArrayList<Object> locationRequests = new ArrayList<>();
        locationRequests.add(locationImportCSVRequest);
        locationImportCSVRequests.add(locationImportCSVRequest);
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forDuplicateLocation();
        when(locationImportValidator.validate(locationImportCSVRequest, locationImportCSVRequests)).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"P1\",\"[Duplicate location in CSV file]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldSaveLocation() {
        ArrayList<Object> locationRequests = new ArrayList<>();
        locationRequests.add(locationImportCSVRequest("D1", "B1", "P1", "VALID"));

        locationImporter.postData(locationRequests);

        verify(locationImportService).addAllWithoutValidations(captor.capture());
        List<LocationImportCSVRequest> locationRequestsToSaveCSV = captor.getValue();
        assertEquals(1, locationRequestsToSaveCSV.size());
        assertEquals("D1", locationRequestsToSaveCSV.get(0).getDistrict());
        assertEquals("B1", locationRequestsToSaveCSV.get(0).getBlock());
        assertEquals("P1", locationRequestsToSaveCSV.get(0).getPanchayat());
        assertEquals(LocationStatus.VALID.getDescription(), locationRequestsToSaveCSV.get(0).getStatus());
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(district, block, panchayat, status, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(district, block, panchayat, status, null, null, null);
    }
}