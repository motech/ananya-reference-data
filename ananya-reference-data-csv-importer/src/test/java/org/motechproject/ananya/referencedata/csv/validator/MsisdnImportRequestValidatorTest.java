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
        assertTrue(response.getMessage().contains("Duplicate records with same msisdn/new msisdn found"));
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
        assertFalse(response.getMessage().contains("Duplicate records with same msisdn/new msisdn found"));
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
        assertTrue(response.getMessage().contains("Duplicate records with same msisdn/new msisdn found"));
    }

    @Test
    public void shouldValidateIfEitherOfNewMsisdnOrAlternateContactNumberIsPresent() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", null, "");
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Either of new msisdn or alternate contact number or both should be present"));
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
        assertTrue(responseMessage.contains("Missing msisdn"));
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
        assertTrue(responseMessage.contains("Invalid msisdn"));
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
        assertTrue(responseMessage.contains("No FLW present in DB with msisdn"));
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
        assertTrue(responseMessage.contains("Duplicate FLWs present with same msisdn"));
    }

    @Test
    public void shouldValidateIfVerificationStatusOfFlwIsInvalidForGivenValidMsisdn() {
        testVerificationStatus(VerificationStatus.INVALID);
    }

    @Test
    public void shouldValidateIfVerificationStatusOfFlwIsOtherForGivenValidMsisdn() {
        testVerificationStatus(VerificationStatus.OTHER);
    }

    private void testVerificationStatus(final VerificationStatus verificationStatus) {
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
        assertTrue(responseMessage.contains("Verification Status of FLW is INVALID or OTHER"));
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
        assertTrue(responseMessage.contains("Duplicate FLWs present with same new msisdn"));
    }

    @Test
    public void shouldValidateForInvalidNewMsisdn() {
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("1234567890", "invalidNewMsisdn", null);
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Invalid new msisdn"));
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
        assertTrue(response.getMessage().contains("Invalid alternate contact number"));
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
    public void shouldReturnResponseWithMultipleErrosIfThereAreMultipleValidationFailures() {
        String msisdn = "1234567890111";
        List<MsisdnImportRequest> requests = new ArrayList<>();
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest(msisdn, null, "");
        requests.add(invalidRequest);
        requests.add(invalidRequest);

        MsisdnImportValidationResponse response = msisdnImportRequestValidator.validate(requests, invalidRequest);

        assertFalse(response.isValid());
        List<String> responseMessage = response.getMessage();
        assertEquals(3, responseMessage.size());
        assertTrue(responseMessage.contains("Duplicate records with same msisdn/new msisdn found"));
        assertTrue(responseMessage.contains("Invalid msisdn"));
        assertTrue(responseMessage.contains("Either of new msisdn or alternate contact number or both should be present"));
    }
}
