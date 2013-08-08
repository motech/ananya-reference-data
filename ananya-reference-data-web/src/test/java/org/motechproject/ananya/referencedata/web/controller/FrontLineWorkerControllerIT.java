package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class FrontLineWorkerControllerIT extends SpringIntegrationTest {

    public static final String VALID_MSISDN = "1234567897";
    public static final String SUCCESS_VERIFICATION = "SUCCESS";
    public static final String CHANNEL = "contact_center";
    public static final String VALID_NAME = "Kumari Manju";
    public static final String NAME = "Name";
    public static final String DEFAULT_GUID = FrontLineWorker.DEFAULT_UUID.toString();


    @Autowired
    FrontLineWorkerController frontLineWorkerController;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllLocations allLocations;


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Location location;
    private LocationRequest locationRequest;


    @Before
    public void setUp() {
        locationRequest = new LocationRequest("District", "Block", "Panchayat", "State");
        location = new Location("State", "District", "Block", "Panchayat", LocationStatus.VALID, null);
        allLocations.add(location);
    }

    @Test
    public void shouldCreateNewFLW() {
        givenNoDuplicateFLWs();
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(DEFAULT_GUID, VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
        verifyUpload(VALID_MSISDN);
    }

    @Test
    public void shouldCreateNewFlwIfDuplicateUnverifiedRecordsExist() {
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb1);
        allFrontLineWorkers.add(frontLineWorkerInDb2);
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(DEFAULT_GUID, VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);

        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);

        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(3, flwInDb.size());
        List<FrontLineWorker> byMsisdnWithStatus = allFrontLineWorkers.getByMsisdnWithStatus(formatPhoneNumber(VALID_MSISDN));
        assertEquals(1, byMsisdnWithStatus.size());
        assertEquals(SUCCESS_VERIFICATION, byMsisdnWithStatus.get(0).getVerificationStatus());
    }

    @Test
    public void shouldUpdateVerifiedFlwIfDuplicateUnverifiedRecordsExist() {
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, "INVALID");
        allFrontLineWorkers.add(frontLineWorkerInDb1);
        allFrontLineWorkers.add(frontLineWorkerInDb2);
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(DEFAULT_GUID, VALID_MSISDN,
                SUCCESS_VERIFICATION, locationRequest, NAME);

        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);

        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN));
        assertEquals(2, flwInDb.size());
        FrontLineWorker byFlwId = allFrontLineWorkers.getByFlwId(frontLineWorkerInDb2.getFlwId());
        assertEquals(SUCCESS_VERIFICATION, byFlwId.getVerificationStatus());
    }

    @Test
    public void shouldUpdateFlwById() {
        FrontLineWorker frontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb);
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(frontLineWorkerInDb.getFlwId().toString(),
                VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);

        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
        verifyUpload(VALID_MSISDN);
    }

    @Test
    public void shouldFailIfVerifiedDuplicateExistsWhenUpdatedById() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Conflicting flw record exists. Please try again later.");
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, "SUCCESS");
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb1);
        allFrontLineWorkers.add(frontLineWorkerInDb2);
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(frontLineWorkerInDb2.getFlwId().toString(),
                VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);

        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
    }

    @Test
    public void shouldFailIfGUIDIsMissing() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("id field is missing");
        givenNoDuplicateFLWs();
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest("", VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());

    }

    @Test
    public void shouldUpdateFlWIfOnlyOneRecordIsPresentInDB() throws Exception {
        String otherMsisdn = "0987654321";
        String otherName = "Simon";
        FrontLineWorker frontLineWorkerInDb1 = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        FrontLineWorker frontLineWorkerInDb2 = new FrontLineWorker(formatPhoneNumber(otherMsisdn), otherName, Designation.ANM, null, "INVALID");
        allFrontLineWorkers.add(frontLineWorkerInDb1);
        allFrontLineWorkers.add(frontLineWorkerInDb2);

        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest1 = createFrontLineWorkerVerificationWebRequest(FrontLineWorker.DEFAULT_UUID.toString(), VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest, NAME);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest1, CHANNEL);
        FrontLineWorker frontLineWorker = verifyUpload(VALID_MSISDN);
        assertEquals(location, frontLineWorker.getLocation());

        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest2 = createFrontLineWorkerVerificationWebRequest(FrontLineWorker.DEFAULT_UUID.toString(), otherMsisdn, SUCCESS_VERIFICATION, locationRequest, otherName);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest2, CHANNEL);
        FrontLineWorker frontLineWorker2 = verifyUpload(otherMsisdn);
        assertEquals(location, frontLineWorker2.getLocation());
    }

    @Test
    public void shouldThrowExceptionIfVerificationStatusIsEmpty() {
        FrontLineWorker frontLineWorkerInDb = new FrontLineWorker(formatPhoneNumber(VALID_MSISDN), VALID_NAME, Designation.ANM, null, null);
        allFrontLineWorkers.add(frontLineWorkerInDb);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("verificationStatus field is missing");

        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(FrontLineWorker.DEFAULT_UUID.toString(), VALID_MSISDN, "", locationRequest, NAME);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
    }

    private FrontLineWorker verifyUpload(String msisdn) {
        List<FrontLineWorker> flwInDb = allFrontLineWorkers.getByMsisdn(formatPhoneNumber(msisdn));
        assertEquals(1, flwInDb.size());
        FrontLineWorker frontLineWorker = flwInDb.get(0);
        assertEquals(SUCCESS_VERIFICATION, frontLineWorker.getVerificationStatus());
        return frontLineWorker;
    }

    private FrontLineWorkerVerificationWebRequest createFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verification, LocationRequest locRequest, String name) {
        return new FrontLineWorkerVerificationWebRequest(flwId, msisdn, verification, name, "ANM", locRequest, null);
    }

    private void givenNoDuplicateFLWs() {
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());
    }

}
