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
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "", "B1", "P1", "");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank state, district, block or panchayat"));
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldFailValidationIfLocationStatusIsInvalid() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", "RandomStatus");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldFailValidationIfStatusIsNotVerified() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.NOT_VERIFIED.getDescription());

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldPassValidationIfAllFieldsArePresent() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.NEW.getDescription());
        when(allLocations.getFor("state", "D1", "B1", "P1")).thenReturn(null);

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldFailValidationForABlankAlternateLocationForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription());

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location not present"));
    }

    @Test
    public void shouldValidateAlternateLocationForLocationsWithInvalidStatusOnly() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        Location location = new Location("S1", "D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null);
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.VALID.getDescription());
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(location);

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForValidAlternateLocationInDBForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(new Location("S1", "D1", "B1", "P1", LocationStatus.NOT_VERIFIED, null));
        when(allLocations.getFor("S2", "D2", "B2", "P2")).thenReturn(new Location("S2", "D2", "B2", "P2", LocationStatus.VALID, null));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S2", "D2", "B2", "P2");

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequests);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForInvalidAlternateLocationInDBForAnInvalidLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequestsFromCsv = new ArrayList<>();
        locationImportRequestsFromCsv.add(locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S2", "D2", "B2", "P2"));
        locationImportRequestsFromCsv.add(locationImportCSVRequest("S3", "D3", "B3", "P3", LocationStatus.VALID.getDescription()));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S2", "D2", "B2", "P2");
        when(allLocations.getFor("S2", "D2", "B2", "P2")).thenReturn(new Location("S2", "D2", "B2", "P2", LocationStatus.NEW, null));
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(new Location("S1", "D1", "B1", "P1", LocationStatus.INVALID, null));

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequestsFromCsv);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location is not a valid/new location"));
    }

    @Test
    public void shouldNotCrapUpIfAlternateLocationHasBlankStatus() {
        ArrayList<LocationImportCSVRequest> locationImportRequestsFromCsv = new ArrayList<>();
        locationImportRequestsFromCsv.add(locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S2", "D2", "B2", "P2"));
        locationImportRequestsFromCsv.add(locationImportCSVRequest("S2", "D2", "B2", "P2", null));
        LocationImportCSVRequest locationRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S2", "D2", "B2", "P2");
        when(allLocations.getFor("S2", "D2", "B2", "P2")).thenReturn(null);
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(new Location("S1", "D1", "B1", "P1", LocationStatus.INVALID, null));

        LocationValidationResponse locationValidationResponse = locationImportValidator.validate(locationRequest, locationImportRequestsFromCsv);

        assertFalse(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().contains("Alternate location is not a valid/new location"));
    }

    @Test
    public void shouldPassValidationIfAlternateLocationIsPresentInCSVAsValidAndNotInDb() {
        String district = "D2";
        String block = "B2";
        String panchayat = "P2";
        String state = "S2";
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("S2", district, block, panchayat, LocationStatus.INVALID.getDescription(), "S1", "D1", "B1", "P1");
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(null);
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(new Location(state, district, block, panchayat, LocationStatus.NOT_VERIFIED, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void shouldPassValidationIfAlternateLocationIsPresentInCSVAsNewAndNotInDb() {
        String district = "D3";
        String block = "B3";
        String panchayat = "P3";
        String state = "S3";
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("S4", "D4", "B4", "P4", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.INVALID.getDescription(), "S4", "D4", "B4", "P4");
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("S4", "D4", "B4", "P4")).thenReturn(null);
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.NOT_VERIFIED, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().isEmpty());
    }

    @Test
    public void shouldValidateForDuplicateEntries() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        locationImportRequests.add(locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.VALID.getDescription()));
        locationImportRequests.add(locationImportCSVRequest("S1", "d1", "B1", "P1", LocationStatus.NEW.getDescription()));
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S4", "D4", "B4", "P4");
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(new Location("S1", "D1", "B1", "P1", LocationStatus.VALID, null));

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
        String state = "S3";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.NEW.getDescription());
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.VALID, null));

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
        String state = "S3";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.VALID.getDescription());
        locationImportRequests.add(locationImportCSVRequest);
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(null);

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Location is not present in DB"));
    }

    @Test
    public void shouldInvalidateLocationRequestIfStatusIsNotInvalidAndItHasAlternateLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.VALID.getDescription(), "S2", "D2", "B2", "P2");

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
        String state = "state";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.VALID.getDescription());
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(new Location(state, district, block, panchayat, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertTrue(response.isValid());
    }

    @Test
    public void shouldFailValidationForAStatusOtherThanNewOnlyIfTransitionIsNotApplicable() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        String state = "state";
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.IN_REVIEW.getDescription());
        when(allLocations.getFor(state, district, block, panchayat)).thenReturn(new Location(state, district, block, panchayat, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportCSVRequest, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Location cannot be updated to the specified status"));
    }

    @Test
    public void shouldNotHaveAlternateLocationSameAsTheLocation() {
        ArrayList<LocationImportCSVRequest> locationImportRequests = new ArrayList<>();
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("S1", "D1", "B1", "P1", LocationStatus.INVALID.getDescription(), "S1", "D1", "B1", "P1");
        when(allLocations.getFor("S1", "D1", "B1", "P1")).thenReturn(new Location("S1", "D1", "B1", "P1", LocationStatus.INVALID, null));

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
        String s1 = "S1";
        String s2 = "S2";
        LocationImportCSVRequest locationImportRequest1 = locationImportCSVRequest(s1, d1, b1, p1, LocationStatus.INVALID.getDescription(), s2, d2, b2, p2);
        LocationImportCSVRequest locationImportRequest2 = locationImportCSVRequest(s2, d2, b2, p2, LocationStatus.INVALID.getDescription(), s1, d1, b1, p1);
        locationImportRequests.add(locationImportRequest1);
        locationImportRequests.add(locationImportRequest2);
        when(allLocations.getFor(s1, d1, b1, p1)).thenReturn(new Location(s1, d1, b1, p1, LocationStatus.VALID, null));
        when(allLocations.getFor(s2, d2, b2, p2)).thenReturn(new Location(s2, d2, b2, p2, LocationStatus.VALID, null));

        LocationValidationResponse response = locationImportValidator.validate(locationImportRequest2, locationImportRequests);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Alternate location is not a valid/new location"));
    }

    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status, String newState, String newDistrict, String newBlock, String newPanchayat) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(state, district, block, panchayat, status, newState, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(state, district, block, panchayat, status, null, null, null, null);
    }
}