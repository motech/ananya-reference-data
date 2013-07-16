package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
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
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class HomeControllerIT extends SpringIntegrationTest {
    public static final String CSV_HEADER = "id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat\n";
    public static final String SUCCESS_VERIFICATION = "SUCCESS";

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

        homeController.uploadFrontLineWorkers(getCsvUploadRequestWithCustomCsvContents("", VALID_MSISDN, SUCCESS_VERIFICATION), null);

        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(1, flwInDb.size());
        assertEquals(SUCCESS_VERIFICATION, flwInDb.get(0).getVerificationStatus());
    }

    @Test
    public void shouldCreateNewFlwWithDefaultGuid() throws Exception {

        givenNoDuplicateFLWs();

        homeController.uploadFrontLineWorkers(getCsvUploadRequestWithCustomCsvContents(FrontLineWorker.DEFAULT_UUID.toString(), VALID_MSISDN, SUCCESS_VERIFICATION), null);

        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(1, flwInDb.size());
        FrontLineWorker frontLineWorker = flwInDb.get(0);
        assertEquals(SUCCESS_VERIFICATION, frontLineWorker.getVerificationStatus());
        assertNotEquals(FrontLineWorker.DEFAULT_UUID.toString(),frontLineWorker.getFlwId());
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
                "%s,%s,1234567893,Kumari Manju,ANM,%s,State,District,Block,Panchayat",
                guid, msisdn, verificationStatus);
    }

}
