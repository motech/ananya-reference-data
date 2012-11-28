package org.motechproject.ananya.referencedata.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.LocationImportCSVRequest;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.utils.LocationImportCSVRequestBuilder;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocationImportValidatorTest {
    @Mock
    AllLocations allLocations;

    private LocationImportValidator locationImportValidator;

    @Before
    public void setUp() {
        locationImportValidator = new LocationImportValidator(allLocations);
    }

    @Test
    public void shouldFailValidationIfLocationFieldsAreBlank() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("", "B1", "P1", "");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank district, block or panchayat"));
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldFailValidationIfLocationStatusIsInvalid() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", "RandomStatus");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldFailValidationIfStatusIsNotVerified() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.NOT_VERIFIED.getDescription());

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldPassValidationIfAllFieldsArePresent() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.NEW.getDescription());
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(null);

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldFailValidationForABlankAlternateLocationForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription());

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location not present"));
    }

    @Test
    public void shouldValidateAlternateLocationForLocationsWithInvalidStatusOnly() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        Location location = new Location("D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null);
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.VALID.getDescription());
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(location);

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForValidAlternateLocationInDBForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(new Location("D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null));
        when(allLocations.getFor("D2", "B2", "P2")).thenReturn(new Location("D2", "B2", "P2", LocationStatus.VALID, null));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForInvalidAlternateLocationInDBForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequestsFromCsv = new ArrayList<>();
        locationImportRequestsFromCsv.add(locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2"));
        locationImportRequestsFromCsv.add(locationImportCSVRequest("D3", "B3", "P3", LocationStatus.VALID.getDescription()));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2");
        when(allLocations.getFor("D2", "B2", "P2")).thenReturn(new Location("D2", "B2", "P2", LocationStatus.NEW, null));

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequestsFromCsv);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location is not a valid/new location"));
    }

    @Test
    public void shouldNotCrapUpIfAlternateLocationHasBlankStatus() {
        ArrayList<LocationImportCSVRequest> locationImportRequestsFromCsv = new ArrayList<>();
        locationImportRequestsFromCsv.add(locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2"));
        locationImportRequestsFromCsv.add(locationImportCSVRequest("D2", "B2", "P2", null));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D2", "B2", "P2");
        when(allLocations.getFor("D2", "B2", "P2")).thenReturn(null);

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequestsFromCsv);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location is not a valid/new location"));
    }

    @Test
    public void shouldPassValidationIfAlternateLocationIsPresentInCSVAsValidAndNotInDb() {
        String district = "D2";
        String block = "B2";
        String panchayat = "P2";
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("D1", "B1", "P1", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.INVALID.getDescription(), "D1", "B1", "P1");
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("D1", "B1", "P1")).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, LocationStatus.NOT_VERIFIED, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void shouldPassValidationIfAlternateLocationIsPresentInCSVAsNewAndNotInDb() {
        String district = "D3";
        String block = "B3";
        String panchayat = "P3";
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("D4", "B4", "P4", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.INVALID.getDescription(), "D4", "B4", "P4");
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("D4", "B4", "P4")).thenReturn(null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, LocationStatus.NOT_VERIFIED, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForDuplicateEntries() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("D1", "B1", "P1", LocationStatus.VALID.getDescription()));
        locationImportRequests.add(locationImportCSVRequest("d1", "B1", "P1", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D4", "B4", "P4");
        locationImportRequests.add(locationImportCSVRequest);

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Duplicate location in CSV file"));
    }

    @Test
    public void shouldFailIfALocationAlreadyExistsInDBForRequestWithNewStatus() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String district = "D3";
        String block = "B3";
        String panchayat = "P3";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.NEW.getDescription());
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Location is already present in DB"));
    }

    @Test
    public void shouldFailIfALocationDoesExistInDBForRequestWithAnyStatusOtherThanNew() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String district = "D3";
        String block = "B3";
        String panchayat = "P3";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.VALID.getDescription());
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(null);

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Location is not present in DB"));
    }

    @Test
    public void shouldInvalidateLocationRequestIfStatusIsNotInvalidAndItHasAlternateLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.VALID.getDescription(), "D2", "B2", "P2");

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Alternate location provided when not required"));
    }

    @Test
    public void shouldUpdateDBForAStatusOtherThanNewOnlyIfTransitionIsApplicable() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.VALID.getDescription());
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
    }

    @Test
    public void shouldFailValidationForAStatusOtherThanNewOnlyIfTransitionIsNotApplicable() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(district, block, panchayat, LocationStatus.IN_REVIEW.getDescription());
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Location cannot be updated to the specified status"));
    }

    @Test
    public void shouldNotHaveAlternateLocationSameAsTheLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "D1", "B1", "P1");

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Alternate location is not a valid/new location"));
    }

    @Test
    public void shouldNotHaveCircularReferenceToAlternateLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String d1 = "D1";
        String b1 = "B1";
        String p1 = "P1";
        String d2 = "D2";
        String b2 = "B2";
        String p2 = "P2";
        LocationImportCSVRequest locationImportRequest1 = locationImportCSVRequest(d1, b1, p1, LocationStatus.INVALID.getDescription(), d2, b2, p2);
        LocationImportCSVRequest locationImportRequest2 = locationImportCSVRequest(d2, b2, p2, LocationStatus.INVALID.getDescription(), d1, b1, p1);
        locationImportRequests.add(locationImportRequest1);
        locationImportRequests.add(locationImportRequest2);
        when(allLocations.getFor(d1, b1, p1)).thenReturn(new Location(d1, b1, p1, LocationStatus.VALID, null));
        when(allLocations.getFor(d2, b2, p2)).thenReturn(new Location(d2, b2, p2, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportRequest2, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Alternate location is not a valid/new location"));
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(district, block, panchayat, status, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(district, block, panchayat, status, null, null, null);
    }
}