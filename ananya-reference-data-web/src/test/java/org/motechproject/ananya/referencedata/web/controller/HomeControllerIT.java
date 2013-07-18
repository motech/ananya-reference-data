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

import static org.apache.commons.lang.StringUtils.*;
import static org.junit.Assert.*;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class HomeControllerIT extends SpringIntegrationTest {
    public static final String CSV_HEADER = "id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat\n";
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

        assertThatItIsSuccessfullyUploadedWithSuccessVerificationStatusAndIsTheOnlyRecord(VALID_MSISDN);
    }


    @Test
    public void shouldCreateNewFlwWithDefaultGuid() throws Exception {

        givenNoDuplicateFLWs();

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithDefaultGUID(VALID_MSISDN, SUCCESS_VERIFICATION), null);

        FrontLineWorker frontLineWorker = assertThatItIsSuccessfullyUploadedWithSuccessVerificationStatusAndIsTheOnlyRecord(VALID_MSISDN);
        assertNotEquals(FrontLineWorker.DEFAULT_UUID.toString(),frontLineWorker.getFlwId());
    }


    @Test
    public void shouldUpdateFlWWithBlankVerificationStatusIfOnlyOneRecordIsPresentInDB() throws Exception {
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null,null);
        allFrontLineWorkers.add(frontLineWorkerInDb1);

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithDefaultGUID(VALID_MSISDN, SUCCESS_VERIFICATION),null);

        assertThatItIsSuccessfullyUploadedWithSuccessVerificationStatusAndIsTheOnlyRecord(VALID_MSISDN);
        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(frontLineWorkerInDb1.getFlwId());
        assertNotNull(frontLineWorker);
        assertEquals(SUCCESS_VERIFICATION, frontLineWorker.getVerificationStatus());
        assertEquals(location,frontLineWorker.getLocation());

        String otherMSISDN = "1231231231";
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(otherMSISDN), "OtherName", Designation.ANM, null,"SUCCESS");
        allFrontLineWorkers.add(frontLineWorkerInDb2);

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithBlankGuid(otherMSISDN),null);

        assertThatItIsSuccessfullyUploadedWithSuccessVerificationStatusAndIsTheOnlyRecord(otherMSISDN);
        FrontLineWorker frontLineWorker2 = allFrontLineWorkers.getByFlwId(frontLineWorkerInDb2.getFlwId());
        assertNotNull(frontLineWorker);
        assertEquals(SUCCESS_VERIFICATION, frontLineWorker2.getVerificationStatus());
        assertEquals(location,frontLineWorker2.getLocation());
    }


    @Test
    public void shouldUpdateFlWWithBlankVerificationStatusIfOnlyOneRecordIsPresentInDBWhenRequestHaveBlankVerificationStatus() throws Exception {
        FrontLineWorker frontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null,null);
        allFrontLineWorkers.add(frontLineWorkerInDb);

        homeController.uploadFrontLineWorkers(createCsvUploadRequestWithDefaultGUID(VALID_MSISDN, ""),null);

        FrontLineWorker frontLineWorker = allFrontLineWorkers.getByFlwId(frontLineWorkerInDb.getFlwId());
        assertNotNull(frontLineWorker);
        assertTrue(isBlank(frontLineWorker.getVerificationStatus()));
        assertEquals(location,frontLineWorker.getLocation());
    }

    private CsvUploadRequest createCsvUploadRequestWithDefaultGUID(String validMsisdn, String verificationStatus) {
        return getCsvUploadRequestWithCustomCsvContents(FrontLineWorker.DEFAULT_UUID.toString(), validMsisdn, verificationStatus);
    }

    private FrontLineWorker assertThatItIsSuccessfullyUploadedWithSuccessVerificationStatusAndIsTheOnlyRecord(String validMsisdn) {
        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(validMsisdn));
        assertEquals(1, flwInDb.size());
        FrontLineWorker frontLineWorker = flwInDb.get(0);
        assertEquals(SUCCESS_VERIFICATION, frontLineWorker.getVerificationStatus());
        return frontLineWorker;
    }

    private CsvUploadRequest createCsvUploadRequestWithBlankGuid(String msisdn) {
        return getCsvUploadRequestWithCustomCsvContents("", msisdn, SUCCESS_VERIFICATION);
    }

    private CsvUploadRequest getCsvUploadRequestWithCustomCsvContents(final String guid, final String msisdn, final String verificationStatus) {
        return new CsvUploadRequest() {
            @Override
            public String getStringContent() {
                return getCsvContent(guid, msisdn, verificationStatus);
            }
        };
    }

    private void givenNoDuplicateFLWs() {
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());
    }

    private String getCsvContent(String guid, String msisdn, String verificationStatus) {
        return String.format(CSV_HEADER +
                "%s,%s,1234567893," + VALID_NAME + "," + VALID_DESIGNATION + ",%s,State,District,Block,Panchayat",
                guid, msisdn, verificationStatus);
    }

}
