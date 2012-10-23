package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.csv.validator.LocationImportValidator;
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
    private ArgumentCaptor<List<LocationRequest>> captor;
    private LocationImporter locationImporter;

    @Before
    public void setUp() {
        initMocks(this);
        locationImporter = new LocationImporter(locationImportService, locationImportValidator);
    }

    @Test
    public void shouldValidateLocationRequests() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));
        when(locationImportValidator.validate(new Location("D1","B1","P1"))).thenReturn(new LocationValidationResponse());

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("disrtict,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfLocationDoesNotHaveAllTheDetails() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        locationRequests.add(new LocationRequest("D1", "B1", null));
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forBlankFieldsInLocation();
        when(locationImportValidator.validate(new Location("D1", "B1", null))).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"null\",\"[Blank district, block or panchayat]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateLocations() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1"));
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();
        locationValidationResponse.forDuplicateLocation();
        when(locationImportValidator.validate(new Location("D1", "B1", "P1"))).thenReturn(locationValidationResponse);

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"D1\",\"B1\",\"P1\",\"[Location already present]\"", validationResponse.getErrors().get(1).getMessage());
    }

    @Test
    public void shouldSaveLocation() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1"));
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));

        locationImporter.postData(locationRequests);

        verify(locationImportService).addAllWithoutValidations(captor.capture());
        List<LocationRequest> locationRequestsToSave = captor.getValue();
        assertEquals(1, locationRequestsToSave.size());
        assertEquals("D1", locationRequestsToSave.get(0).getDistrict());
        assertEquals("B1", locationRequestsToSave.get(0).getBlock());
        assertEquals("P1", locationRequestsToSave.get(0).getPanchayat());
    }
}
