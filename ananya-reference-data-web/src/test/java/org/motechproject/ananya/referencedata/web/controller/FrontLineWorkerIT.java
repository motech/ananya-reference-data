package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.web.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FrontLineWorkerIT extends SpringIntegrationTest {

    @Autowired
    private FrontLineWorkerController frontLineWorkerController;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    private UUID flwId;
    private FrontLineWorkerVerificationWebRequest webRequest;

    @Before
    public void setUp() {
        flwId = UUID.randomUUID();
    }

    @Test
    public void shouldSaveFrontLineWorkerWithoutState() {
        createFlw(null, "1234567891");
        assertEquals("Bihar", allFrontLineWorkers.getByFlwId(flwId).getLocation().getState());
    }

    @Test
    public void shouldSaveFrontLineWorkerWithState() {
        String state = "Orissa";
        createFlw(state, "1234567892");
        assertEquals(state, allFrontLineWorkers.getByFlwId(flwId).getLocation().getState());
    }

    private void createFlw(String state, String msisdn) {
        LocationRequest location = new LocationRequest("D1", "B1", "P1", state);
        webRequest = new FrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn,
                VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), location, null);
        frontLineWorkerController.updateVerifiedFlw(webRequest, "contact_center");
    }
}