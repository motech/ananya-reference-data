package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.csv.response.MsisdnImportValidationResponse;
import org.motechproject.ananya.referencedata.csv.service.MsisdnImportService;
import org.motechproject.ananya.referencedata.csv.validator.MsisdnImportRequestValidator;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MsisdnImporterTest {

    @Mock
    private MsisdnImportRequestValidator msisdnImportRequestValidator;
    @Mock
    private MsisdnImportService msisdnImportService;

    private MsisdnImporter msisdnImporter;

    @Before
    public void setUp() throws Exception {
        msisdnImporter = new MsisdnImporter(msisdnImportRequestValidator, msisdnImportService);
    }

    @Test
    public void shouldReturnValidationResponseWithErrorCSVMessageAndInvalidRecords() {
        MsisdnImportRequest invalidRequest = new MsisdnImportRequest("msisdn0987", "newmsisdn1234", "1234567890");
        MsisdnImportRequest validRequest = new MsisdnImportRequest("1234567890", "1234567891", "1234567892");
        List<MsisdnImportRequest> msisdnImportRequests = asList(invalidRequest, validRequest);
        MsisdnImportValidationResponse validationResponseForInvalidRequest = new MsisdnImportValidationResponse();
        validationResponseForInvalidRequest.forInvalidMsisdn();
        validationResponseForInvalidRequest.forInvalidNewMsisdn();
        when(msisdnImportRequestValidator.validate(msisdnImportRequests, invalidRequest)).thenReturn(validationResponseForInvalidRequest);
        when(msisdnImportRequestValidator.validate(msisdnImportRequests, validRequest)).thenReturn(new MsisdnImportValidationResponse());

        ValidationResponse validationResponse = msisdnImporter.validate(msisdnImportRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("msisdn,new_msisdn,alternate_contact_number,error", validationResponse.getErrors().get(0).getMessage());
        assertEquals("\"msisdn0987\",\"newmsisdn1234\",\"1234567890\",\"[MSISDN is not in a valid format, New MSISDN is not in a valid format]\"", validationResponse.getErrors().get(1).getMessage());
        assertEquals(1, validationResponse.getInvalidRecords().size());
        assertEquals(invalidRequest, validationResponse.getInvalidRecords().get(0));
    }

    @Test
    public void shouldReturnValidationResponseWithoutAnyErrorsIfAllRecordsAreValid() {
        MsisdnImportRequest request1 = new MsisdnImportRequest();
        MsisdnImportRequest request2 = new MsisdnImportRequest();
        List<MsisdnImportRequest> requests = asList(request1, request2);
        when(msisdnImportRequestValidator.validate(requests, request1)).thenReturn(new MsisdnImportValidationResponse());
        when(msisdnImportRequestValidator.validate(requests, request2)).thenReturn(new MsisdnImportValidationResponse());

        ValidationResponse validationResponse = msisdnImporter.validate(requests);

        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrors().isEmpty());
        assertTrue(validationResponse.getInvalidRecords().isEmpty());
    }

    @Test
    public void shouldUpdateFLWsWithMsisdnRequestWithoutValidation() {
        List<MsisdnImportRequest> msisdnImportRequests = asList(new MsisdnImportRequest(), new MsisdnImportRequest());

        msisdnImporter.postData(msisdnImportRequests);

        verify(msisdnImportService).updateFLWContactDetailsWithoutValidations(msisdnImportRequests);
    }
}
