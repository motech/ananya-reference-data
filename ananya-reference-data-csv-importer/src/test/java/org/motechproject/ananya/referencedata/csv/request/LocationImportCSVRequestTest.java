package org.motechproject.ananya.referencedata.csv.request;

import org.junit.Test;
import org.motechproject.ananya.referencedata.csv.response.LocationValidationResponse;
import org.motechproject.ananya.referencedata.csv.utils.LocationImportCSVRequestBuilder;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;

import static org.junit.Assert.*;

public class LocationImportCSVRequestTest {

    @Test
    public void shouldValidateExistenceOfAlternateLocation() {
        LocationImportCSVRequest CSVRequest = locationImportCSVRequest("s", "d", "b", "p", LocationStatus.INVALID.getDescription());
        assertFalse(CSVRequest.hasAlternateLocation());

        CSVRequest = locationImportCSVRequest("s", "d", "b", "p", LocationStatus.INVALID.name(), "d1", "b1", "p1", "s1");
        assertTrue(CSVRequest.hasAlternateLocation());
        CSVRequest = locationImportCSVRequest("s", "d", "b", "p", LocationStatus.INVALID.name(), "d1", "b1", "p1", null);
        assertFalse(CSVRequest.hasAlternateLocation());
    }

    @Test
    public void shouldVerifyIfLocationMatchesWithRequestLocation() {
        String district = "d";
        String block = "b";
        String panchayat = "p";
        String state = "s";
        LocationImportCSVRequest CSVRequest = locationImportCSVRequest(state, district, block, panchayat, LocationStatus.INVALID.getDescription());

        assertTrue(CSVRequest.matchesLocation(state, district, block, panchayat));
        assertFalse(CSVRequest.matchesLocation("s1", district, block, panchayat));
        assertFalse(CSVRequest.matchesLocation("s1", "d1", "b1", "p1"));
    }

    @Test
    public void shouldCheckEqualityWithCaseInsensitiveMatch() {
        LocationImportCSVRequest CSVRequest1 = locationImportCSVRequest("state", "district", "block", "panchayat", "status1", "newdistrict1", "newblock1", "newpanchayat1", "newstate1");
        LocationImportCSVRequest CSVRequest2 = locationImportCSVRequest("State", "District", "BlocK", "PanchaYat", "status2", "newdistrict2", "newblock2", "newpanchayat2", "newstate2");

        assertTrue(CSVRequest1.equals(CSVRequest2));
    }

    @Test
    public void shouldValidateACorrectRequest() {
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("s", "d", "b", "p", "valid");
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();

        locationImportCSVRequest.validate(locationValidationResponse);

        assertTrue(locationValidationResponse.isValid());
        assertTrue(locationValidationResponse.getMessage().isEmpty());
    }

    @Test
    public void shouldInvalidateAnIncorrectRequest() {
        LocationImportCSVRequest locationImportCSVRequest = new LocationImportCSVRequest();
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();

        locationImportCSVRequest.validate(locationValidationResponse);

        assertTrue(locationValidationResponse.isInValid());
        assertTrue(locationValidationResponse.getMessage().contains("Blank state, district, block or panchayat"));
        assertTrue(locationValidationResponse.getMessage().contains("Blank or Invalid status"));
    }

    @Test
    public void shouldReturnStatusEnum() {
        assertEquals(LocationStatus.VALID, locationImportCSVRequest(null, null, null, null, "valid").getStatusEnum());
        assertEquals(LocationStatus.IN_REVIEW, locationImportCSVRequest(null, null, null, null, " in revIEW   ").getStatusEnum());
        assertNull(locationImportCSVRequest(null, null, null, null, "invalid status").getStatusEnum());
        assertNull(locationImportCSVRequest(null, null, null, null, null).getStatusEnum());
    }

    @Test
    public void shouldReturnHeaderRowWithErrorsColumn() {
        String headerRowForErrors = new LocationImportCSVRequest().getHeaderRowForErrors();

        assertEquals("state,district,block,panchayat,status,newState,newDistrict,newBlock,newPanchayat,error", headerRowForErrors);
    }

    @Test
    public void shouldConvertToCSV() {
        LocationImportCSVRequest locationImportCSVRequest = locationImportCSVRequest("state", "district", "block", "panchayat", "status1","newdistrict1", "newblock1", "newpanchayat1", "newstate1");
        String expected = "\"state\",\"district\",\"block\",\"panchayat\",\"status1\"," +
                "\"newstate1\",\"newdistrict1\",\"newblock1\",\"newpanchayat1\"";

        assertEquals(expected, locationImportCSVRequest.toCSV());
    }
    
    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status, String newDistrict, String newBlock, String newPanchayat, String newState) {
        return new LocationImportCSVRequestBuilder().withDefaults().buildWith(state, district, block, panchayat, status, newState, newDistrict, newBlock, newPanchayat);
    }

    private LocationImportCSVRequest locationImportCSVRequest(String state, String district, String block, String panchayat, String status) {
        return locationImportCSVRequest(state, district, block, panchayat, status, null, null, null, null);
    }
}