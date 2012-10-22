package org.motechproject.ananya.referencedata.contactCenter.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.contactCenter.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class FrontLineWorkerServiceIT extends SpringIntegrationTest {

    @Autowired
    private FrontLineWorkerService frontLineWorkerService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldUpdateAnExistingFlw() {
        String flwId = UUID.randomUUID().toString();
        Location location = new Location("d", "b", "p");
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "Shahrukh", Designation.ANM, location, flwId, VerificationStatus.INVALID, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, VerificationStatus.OTHERS.name(), "Out of town");

        frontLineWorkerService.updateVerifiedFlw(frontLineWorkerWebRequest);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(frontLineWorkerWebRequest.getVerificationStatus(), updatedFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorkerWebRequest.getReason(), updatedFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getName(), updatedFrontLineWorker.getName());
        assertEquals(frontLineWorker.getMsisdn(), updatedFrontLineWorker.getMsisdn());
        assertEquals(frontLineWorker.getDesignation(), updatedFrontLineWorker.getDesignation());
        assertEquals(frontLineWorker.getLocation(), updatedFrontLineWorker.getLocation());
    }

    @Test
    public void shouldInvalidateAnFlwWhenFLwIdDoesNotExist() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("FLW-Id is not present in MoTeCH");

        Location location = new Location("d", "b", "p");
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "Shahrukh", Designation.ANM, location, UUID.randomUUID().toString(), VerificationStatus.INVALID, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest("newFlwId", VerificationStatus.OTHERS.name(), "Out of town");

        frontLineWorkerService.updateVerifiedFlw(frontLineWorkerWebRequest);
    }
}