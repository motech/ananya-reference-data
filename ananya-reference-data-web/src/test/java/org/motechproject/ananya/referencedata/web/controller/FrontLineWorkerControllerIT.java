package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.motechproject.ananya.referencedata.flw.utils.PhoneNumber.formatPhoneNumber;

public class FrontLineWorkerControllerIT extends SpringIntegrationTest{

    public static final String VALID_MSISDN = "1234567897";
    public static final String SUCCESS_VERIFICATION = "SUCCESS";
    public static final String CHANNEL = "contact_center";

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
        locationRequest = new LocationRequest("District","Block","Panchayat","State");
        location = new Location("District", "Block", "Panchayat", "State", LocationStatus.VALID, null);
        allLocations.add(location);
    }

    @Test
    public void shouldCreateNewFLW(){
        givenNoDuplicateFLWs();
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest(FrontLineWorker.DEFAULT_UUID.toString(), VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest);
        frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
        assertEquals(1, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());

    }

    @Test
    public void shouldFailIfGUIDIsMissing(){
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("id field is missing");
        givenNoDuplicateFLWs();
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = createFrontLineWorkerVerificationWebRequest("", VALID_MSISDN, SUCCESS_VERIFICATION, locationRequest);
            frontLineWorkerController.updateVerifiedFlw(frontLineWorkerWebRequest, CHANNEL);
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());

    }

    private FrontLineWorkerVerificationWebRequest createFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verification, LocationRequest locRequest) {
        return new FrontLineWorkerVerificationWebRequest(flwId, msisdn, verification, "Name", "ANM", locRequest, null);
    }

    private void givenNoDuplicateFLWs() {
        assertEquals(0, allFrontLineWorkers.getByMsisdn(formatPhoneNumber(VALID_MSISDN)).size());
    }

}
