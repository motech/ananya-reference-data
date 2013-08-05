package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.web.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class HomeControllerIT extends SpringIntegrationTest {
    public static final String CSV_HEADER = "id,msisdn,name,designation,verification_status,state,district,block,panchayat\n";
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
        location = new Location("District", "Block", "Panchayat", "State", LocationStatus.VALID, null);
        allLocations.add(location);
    }

    @Test
    public void shouldUseLimitInPropertiesFile() {
        assertTrue(homeController.exceedsMaximumNumberOfRecords(HomeControllerTest.createCSVRecordsWith(501)));
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

    private CsvUploadRequest createCsvUploadRequest(String validMsisdn, String verificationStatus, String guid) {
        String csvContent = getCsvContent(getCsvRows(guid, validMsisdn, verificationStatus));
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
        return String.format(CSV_HEADER + "%s",rows);
    }

    private String getCsvRows(String guid, String msisdn, String verificationStatus) {
        return String.format(
                "%s,%s," + VALID_NAME + "," + VALID_DESIGNATION + ",%s,State,District,Block,Panchayat\n",
                guid, msisdn, verificationStatus);
    }

}
