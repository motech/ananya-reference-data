package org.motechproject.ananya.referencedata.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.csv.response.MsisdnImportValidationResponse;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MsisdnImportRequestValidatorTest {

    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;

    private MsisdnImportRequestValidator msisdnImportRequestValidator;

    @Before
    public void setUp() {
        msisdnImportRequestValidator = new MsisdnImportRequestValidator(allFrontLineWorkers);
    }

    @Test
    public void shouldValidateForDuplicateRecordsByMsisdn() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", "1234567891", null);
        requests.add(invalidRequest);
        requests.add(new MsisdnImportRequest("1234567890", "1234567891", null));
        requests.add(new MsisdnImportRequest("1234567891", "1234567891", null));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("There are duplicate rows in CSV for MSISDN"));
    }

    @Test
    public void shouldNotCheckForDuplicateRecordsIfMsisdnIsBlank() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("", "1234567891", null);
        requests.add(invalidRequest);
        requests.add(new MsisdnImportRequest("", "1234567892", null));
        requests.add(new MsisdnImportRequest("1234567891", "1234567893", null));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertFalse(response.getMessage().contains("There are duplicate rows in CSV for MSISDN"));
    }

    @Test
    public void shouldValidateForDuplicateRecordsByNewMsisdn() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", "1234567893", null);
        requests.add(invalidRequest);
        requests.add(new MsisdnImportRequest("1234567891", "1234567893", null));
        requests.add(new MsisdnImportRequest("1234567892", "1234567894", null));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("There are duplicate rows in CSV for New MSISDN"));
    }

    @Test
    public void shouldValidateIfEitherOfNewMsisdnOrAlternateContactNumberIsPresent() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", null, "");
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("At least one of the updates, new msisdn or alternate contact number, should be present"));
    }

    @Test
    public void shouldValidateIfMsisdnIsPresent() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(null, "1234567891", null);
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains("MSISDN is not provided"));
        verify(allFrontLineWorkers, never()).getByMsisdn(null);
    }

    @Test
    public void shouldValidateForInvalidMsisdn() {
        String msisdn = "1234567890123";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, "1234567891", null);
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains("MSISDN is not in a valid format"));
        verify(allFrontLineWorkers, never()).getByMsisdn(Long.valueOf(msisdn));
    }

    @Test
    public void shouldValidateIfFlwExistsForGivenValidMsisdn() {
        String msisdn = "1234567890";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, "1234567891", null);
        requests.add(invalidRequest);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(Collections.EMPTY_LIST);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains("Could not find an FLW record in database with provided MSISDN"));
    }

    @Test
    public void shouldValidateIfDuplicateFlwsExistsForGivenValidMsisdn() {
        String msisdn = "911234567890";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, "1234567891", null);
        requests.add(invalidRequest);
        List<FrontLineWorker> frontLineWorkers = asList(new FrontLineWorker(), new FrontLineWorker());
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(frontLineWorkers);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains("Duplicate FLW records are present in database for provided MSISDN"));
    }

    @Test
    public void shouldValidateIfVerificationStatusOfFlwIsInvalidForGivenValidMsisdn() {
        testVerificationStatus(VerificationStatus.INVALID, "Verification Status of FLW for provided MSISDN is INVALID");
    }

    @Test
    public void shouldValidateIfVerificationStatusOfFlwIsOtherForGivenValidMsisdn() {
        testVerificationStatus(VerificationStatus.OTHER, "Verification Status of FLW for provided MSISDN is OTHER");
    }

    private void testVerificationStatus(VerificationStatus verificationStatus, String errorMessage) {
        String msisdn = "911234567890";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, "911234567891", null);
        requests.add(invalidRequest);
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setVerificationStatus(verificationStatus);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(asList(frontLineWorker));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains(errorMessage));
    }

    @Test
    public void shouldValidateIfDuplicateFlwsExistsWithGivenNewMsisdn() {
        String msisdn = "911234567890";
        String newMsisdn = "911234567891";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, newMsisdn, null);
        requests.add(invalidRequest);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(asList(new FrontLineWorker()));
        List<FrontLineWorker> frontLineWorkers = asList(new FrontLineWorker(), new FrontLineWorker());
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(newMsisdn))).thenReturn(frontLineWorkers);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(1, responseMessage.size());
        assertTrue(responseMessage.contains("Duplicate FLW records present in database for provided New MSISDN"));
    }

    @Test
    public void shouldValidateForInvalidNewMsisdn() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", "invalidNewMsisdn", null);
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("New MSISDN is not in a valid format"));
    }

    @Test
    public void shouldNotValidateIfNewMsisdnIsPresent() {
        String msisdn = "911234567890";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, null, "1234567891");
        requests.add(invalidRequest);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(asList(new FrontLineWorker()));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertTrue(response.isValid());
    }

    @Test
    public void shouldValidateForInvalidAlternateContactNumber() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", null, "invalidAlternateContactNumber");
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Alternate contact number is not in a valid format"));
    }

    @Test
    public void shouldNotValidateIfAlternateContactNumberIsPresent() {
        String msisdn = "911234567890";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, "911234567891", "");
        requests.add(invalidRequest);
        when(allFrontLineWorkers.getByMsisdn(Long.valueOf(msisdn))).thenReturn(asList(new FrontLineWorker()));

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertTrue(response.isValid());
    }

    @Test
    public void shouldReturnResponseWithMultipleErrorsIfThereAreMultipleValidationFailures_AndImplicitlyChecksIfEqualsIsNotOverridden() {
        String msisdn = "1234567890111";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, null, "");
        MsisdnImportRequest requestWithSameValues = new MsisdnImportRequest(msisdn, null, "");
        requests.add(invalidRequest);
        requests.add(requestWithSameValues);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(3, responseMessage.size());
        assertTrue(responseMessage.contains("There are duplicate rows in CSV for MSISDN"));
        assertTrue(responseMessage.contains("MSISDN is not in a valid format"));
        assertTrue(responseMessage.contains("At least one of the updates, new msisdn or alternate contact number, should be present"));
    }

    @Test
    public void shouldValidateIfMsisdnOfGivenRecordIsSameAsNewMsisdnOfAnyOtherRecord() {
        String msisdn = "1234567890";
        MsisdnImportRequest request1 = new MsisdnImportRequest(msisdn, "1234567891", "");
        MsisdnImportRequest request2 = new MsisdnImportRequest("9876543210", msisdn, null);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(asList(request1, request2), request1);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("There is another record in CSV with New MSISDN same as provided MSISDN"));
    }

    @Test
    public void shouldValidateIfNewMsisdnOfGivenRecordIsSameAsMsisdnOfAnyOtherRecord() {
        String newMsisdn = "1234567891";
        MsisdnImportRequest request1 = new MsisdnImportRequest("1234567890", newMsisdn, "");
        MsisdnImportRequest request2 = new MsisdnImportRequest(newMsisdn, "9876543210", null);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(asList(request1, request2), request1);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("There is another record in CSV with MSISDN same as provided New MSISDN"));
    }
}
