package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.motechproject.ananya.referencedata.flw.service.LocationService;
import org.motechproject.ananya.referencedata.flw.validators.LocationValidator;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationImporterTest {

    @Mock
    private LocationService locationService;
    @Mock
    private LocationValidator locationValidator;
    @Captor
    private ArgumentCaptor<List<LocationRequest>> captor;
    private LocationImporter locationImporter;

    @Before
    public void setUp() {
        initMocks(this);
        locationImporter = new LocationImporter(locationService, locationValidator);
    }

    @Test
    public void shouldValidateLocationRequests() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));
        when(locationValidator.validate(new Location("D1","B1","P1"))).thenReturn(new FLWValidationResponse());

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("disrtict,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfLocationDoesNotHaveAllTheDetails() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        locationRequests.add(new LocationRequest("D1", "B1", null));
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        flwValidationResponse.forBlankFieldsInLocation();
        when(locationValidator.validate(new Location("D1", "B1", null))).thenReturn(flwValidationResponse);

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
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        flwValidationResponse.forDuplicateLocation();
        when(locationValidator.validate(new Location("D1", "B1", "P1"))).thenReturn(flwValidationResponse);

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

        verify(locationService).addAllWithoutValidations(captor.capture());
        List<LocationRequest> locationRequestsToSave = captor.getValue();
        assertEquals(1, locationRequestsToSave.size());
        assertEquals("D1", locationRequestsToSave.get(0).getDistrict());
        assertEquals("B1", locationRequestsToSave.get(0).getBlock());
        assertEquals("P1", locationRequestsToSave.get(0).getPanchayat());
    }
}
