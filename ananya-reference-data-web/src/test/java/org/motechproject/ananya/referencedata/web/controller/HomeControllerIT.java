package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.web.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class HomeControllerIT extends SpringIntegrationTest {
    public static final String CSV_HEADER = "id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat\n";
    public static final String MSISDN_CSV_HEADER = "msisdn,new_msisdn,alternate_contact_number\n";
    public static final String SUCCESS_VERIFICATION = "SUCCESS";
    public static final String VALID_NAME = "Kumari Manju";
    public static final String VALID_DESIGNATION = "ANM";

    @Autowired
    private HomeController homeController;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllLocations allLocations;

    private Location location;
    public static final String VALID_MSISDN = "1234567897";

    @Before
    public void setUp() {
        location = new Location("State", "District", "Block", "Panchayat", LocationStatus.VALID, null);
        allLocations.add(location);
    }

    @Test
    public void shouldUseLimitInPropertiesFileAndReturnErrorResponse() throws Exception {
        ModelAndView modelAndView = homeController.uploadFrontLineWorkers(
                createCsvUploadRequestWithMultipleRows(VALID_MSISDN, SUCCESS_VERIFICATION, FrontLineWorker.DEFAULT_UUID.toString(), 6), null);

        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("FLW file can have a maximum of 5 records.", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    public void shouldCreateNewFLW() throws Exception {
        givenNoDuplicateFLWs();

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithBlankGuid(VALID_MSISDN), null);

        verifySuccessfulUpload(VALID_MSISDN, SUCCESS_VERIFICATION);
    }


    @Test
    public void shouldCreateNewFlwWithDefaultGuid() throws Exception {
        givenNoDuplicateFLWs();

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, SUCCESS_VERIFICATION, FrontLineWorker.DEFAULT_UUID.toString()), null);

        verifySuccessfulUpload(VALID_MSISDN, SUCCESS_VERIFICATION);
    }


    @Test
    public void shouldUpdateStatusOfTheSingleFlWWithBlankVerificationStatusInDB() throws Exception {
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb1);

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, SUCCESS_VERIFICATION, FrontLineWorker.DEFAULT_UUID.toString()), null);

        verifySuccessfulUpload(VALID_MSISDN, SUCCESS_VERIFICATION);

        String otherMSISDN = "1231231231";
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(otherMSISDN), "OtherName", Designation.ANM, null, "SUCCESS");
        allFrontLineWorkers.add(frontLineWorkerInDb2);

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithBlankGuid(otherMSISDN), null);

        verifySuccessfulUpload(otherMSISDN, SUCCESS_VERIFICATION);
    }

    @Test
    public void shouldUpdateTheSingleFlWWithBlankVerificationStatusInDB() throws Exception {
        FrontLineWorker frontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb);

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, "", FrontLineWorker.DEFAULT_UUID.toString()), null);

        verifySuccessfulUpload(VALID_MSISDN, null);
    }

    @Test
    public void shouldUpdateVerifiedFLWIncaseOfDuplicatesInDb() throws Exception {
        FrontLineWorker frontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        FrontLineWorker verifiedFrontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, "INVALID");
        allFrontLineWorkers.add(frontLineWorkerInDb);
        allFrontLineWorkers.add(verifiedFrontLineWorkerInDb);

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, SUCCESS_VERIFICATION, FrontLineWorker.DEFAULT_UUID.toString()), null);

        List<FrontLineWorker> flwsInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(2, flwsInDb.size());
        FrontLineWorker flwInDb = allFrontLineWorkers.getByFlwId(verifiedFrontLineWorkerInDb.getFlwId());
        assertEquals(SUCCESS_VERIFICATION, flwInDb.getVerificationStatus());
    }

    @Test
    public void shouldUpdateFLWIncaseOfNonVerifiedFLWInDb() throws Exception {
        FrontLineWorker verifiedFrontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(verifiedFrontLineWorkerInDb);
        String guid = verifiedFrontLineWorkerInDb.getFlwId().toString();

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, SUCCESS_VERIFICATION, guid), null);

        verifySuccessfulUpload(VALID_MSISDN, SUCCESS_VERIFICATION);
    }

    @Test
    public void shouldUpdateFLWIncaseOfVerifiedFLWInDb() throws Exception {
        FrontLineWorker verifiedFrontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, "INVALID");
        allFrontLineWorkers.add(verifiedFrontLineWorkerInDb);
        String guid = verifiedFrontLineWorkerInDb.getFlwId().toString();

        homeController.uploadFrontLineWorkers(createCsvUploadRequest(VALID_MSISDN, SUCCESS_VERIFICATION, guid), null);

        verifySuccessfulUpload(VALID_MSISDN, SUCCESS_VERIFICATION);
    }

    @Test
    public void shouldCreateNewFLWIncaseOfDuplicatesInCSV() throws Exception {
        String rows = getCsvRows("", VALID_MSISDN, "") + getCsvRows("", VALID_MSISDN, "");
        CsvUploadRequest request = getCsvUploadRequestWithCustomCsvContents(getCsvContent(rows));

        homeController.uploadFrontLineWorkers(request, null);

        List<FrontLineWorker> flwsInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(2, flwsInDb.size());
    }

    @Test
    public void shouldChangeMSISDNOfFLWsOfValidRecordsInCSVAndThrowErrorsForInvalidRecords() throws Exception {
        String msisdn1 = "919876543210";
        String msisdn2 = "919876543211";
        String newMsisdn1 = "919999999999";
        String newMsisdn2 = "8888888888";
        String alternateContactNumber1 = "911122334455";
        String alternateContactNumber2 = "6677889900";
        createFLWs(msisdn1, msisdn2);
        CsvUploadRequest csvRequest = createMsisdnCsvRequest(asList(msisdn1, msisdn2), asList(newMsisdn1, newMsisdn2), asList(alternateContactNumber1, alternateContactNumber2));
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        String expectedErrorCsvContent = "msisdn,new_msisdn,alternate_contact_number,error\n" +
                "\"invalidMsisdn\",\"1234567890\",\"\",\"[Invalid msisdn]\"\n" +
                "\"1234567890\",\"\",\"\",\"[Either of new msisdn or alternate contact number or both should be present, No FLW present in DB with msisdn]\"\n";


        homeController.uploadMSISDNs(csvRequest, response);

        assertFLWDetailsAfterImport(msisdn1, newMsisdn1);
        assertFLWDetailsAfterImport(msisdn2, newMsisdn2);

        verify(outputStream).write(expectedErrorCsvContent.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=msisdn_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
    }

    private void createFLWs(String msisdn1, String msisdn2) {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(UUID.randomUUID());
        frontLineWorker1.setMsisdn(PhoneNumber.formatPhoneNumber(msisdn1));
        FrontLineWorker frontLineWorker2 = new FrontLineWorker(UUID.randomUUID());
        frontLineWorker2.setMsisdn(PhoneNumber.formatPhoneNumber(msisdn2));
        allFrontLineWorkers.createOrUpdateAll(asList(frontLineWorker1, frontLineWorker2));
    }

    private CsvUploadRequest createMsisdnCsvRequest(List<String> validMsisdns, List<String> validNewMsisdns, List<String> validAlternateContactNumbers) {
        String validCsvRecords = getMsisdnCsvRecords(validMsisdns, validNewMsisdns, validAlternateContactNumbers);
        String invalidRecords = "invalidMsisdn,1234567890, \n1234567890,,\n";

        String csvContent = String.format("%s%s%s", MSISDN_CSV_HEADER, validCsvRecords, invalidRecords);
        return getCsvUploadRequestWithCustomCsvContents(csvContent);
    }

    private String getMsisdnCsvRecords(List<String> validMsisdns, List<String> validNewMsisdns, List<String> validAlternateContactNumbers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int csvRowCounter = 0; csvRowCounter < validMsisdns.size() && csvRowCounter < validNewMsisdns.size(); csvRowCounter++) {
            stringBuilder.append(String.format("%s,%s,%s\n", validMsisdns.get(csvRowCounter), validNewMsisdns.get(csvRowCounter), validAlternateContactNumbers.get(csvRowCounter)));
        }
        return stringBuilder.toString();
    }

    private void assertFLWDetailsAfterImport(String msisdn, String newMsisdn) {
        assertTrue(allFrontLineWorkers.getByMsisdn(PhoneNumber.formatPhoneNumber(msisdn)).isEmpty());
        List<FrontLineWorker> flwByNewMsisdn = allFrontLineWorkers.getByMsisdn(PhoneNumber.formatPhoneNumber(newMsisdn));
        assertEquals(1, flwByNewMsisdn.size());
    }

    private CsvUploadRequest createCsvUploadRequest(String validMsisdn, String verificationStatus, String guid) {
        String csvContent = getCsvContent(getCsvRows(guid, validMsisdn, verificationStatus));
        return getCsvUploadRequestWithCustomCsvContents(csvContent);
    }

    private CsvUploadRequest createCsvUploadRequestWithMultipleRows(String validMsisdn, String verificationStatus, String guid, int numberOfRows) {
        String csvContent = getCsvContent(getCsvRows(guid, validMsisdn, verificationStatus, numberOfRows));
        return getCsvUploadRequestWithCustomCsvContents(csvContent);
    }

    private void verifySuccessfulUpload(String validMsisdn, String verificationStatus) {
        List<FrontLineWorker> flwsInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(validMsisdn));
        assertEquals(1, flwsInDb.size());
        FrontLineWorker frontLineWorker = flwsInDb.get(0);
        assertEquals(verificationStatus, frontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getLocation(), location);
        assertEquals(frontLineWorker.getReason(), "via CSV Upload");
        assertNotEquals(FrontLineWorker.DEFAULT_UUID.toString(), frontLineWorker.getFlwId());
    }

    private CsvUploadRequest createCsvUploadRequestWithBlankGuid(String msisdn) {
        String csvContent = getCsvContent(getCsvRows("", msisdn, SUCCESS_VERIFICATION));
        return getCsvUploadRequestWithCustomCsvContents(csvContent);
    }

    private CsvUploadRequest getCsvUploadRequestWithCustomCsvContents(final String csvContent) {
        return new CsvUploadRequest() {
            @Override
            public String getStringContent() {
                return csvContent;
            }
        };
    }

    private void givenNoDuplicateFLWs() {
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());
    }

    private String getCsvContent(String rows) {
        return String.format(CSV_HEADER + "%s", rows);
    }

    private String getCsvRows(String guid, String msisdn, String verificationStatus) {
        return getCsvRows(guid, msisdn, verificationStatus, 1);
    }

    private String getCsvRows(String guid, String msisdn, String verificationStatus, int numberOfRows) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rowCount = 0; rowCount < numberOfRows; rowCount++) {
            stringBuilder.append(String.format(
                    "%s,%s,1234567893," + VALID_NAME + "," + VALID_DESIGNATION + ",%s,State,District,Block,Panchayat\n",
                    guid, msisdn, verificationStatus));
        }
        return stringBuilder.toString();
    }
}
