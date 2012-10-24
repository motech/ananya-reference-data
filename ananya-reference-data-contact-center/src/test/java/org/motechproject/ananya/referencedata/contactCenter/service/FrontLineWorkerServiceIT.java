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
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
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
    public void shouldUpdateAnExistingFlwDuringUnsuccessfulRegistration() {
        String flwId = UUID.randomUUID().toString();
        Location location = new Location("d", "b", "p", "VALID");
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
    public void shouldUpdateAnExistingFlwDuringSuccessfulRegistration() {
        String flwId = UUID.randomUUID().toString();
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "Shahrukh", null, null, flwId, VerificationStatus.INVALID, "reason");
        String name = "New Name";
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat", null);
        Location location = LocationMapper.mapFrom(locationRequest);
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        template.flush();
        String designation = Designation.ANM.name();
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, VerificationStatus.SUCCESS.name(), name, designation, locationRequest);

        frontLineWorkerService.updateVerifiedFlw(frontLineWorkerWebRequest);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(frontLineWorker.getFlwId(), updatedFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), updatedFrontLineWorker.getMsisdn());
        assertEquals(VerificationStatus.SUCCESS.name(), updatedFrontLineWorker.getVerificationStatus());
        assertEquals(name, updatedFrontLineWorker.getName());
        assertEquals(designation, updatedFrontLineWorker.getDesignation());
        assertEquals(location, updatedFrontLineWorker.getLocation());
        assertEquals(null, updatedFrontLineWorker.getReason());
    }

    @Test
    public void shouldInvalidateAnFlwWhenFLwIdDoesNotExist() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("FLW-Id is not present in MoTeCH");

        Location location = new Location("d", "b", "p", "VALID");
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "Shahrukh", Designation.ANM, location, UUID.randomUUID().toString(), VerificationStatus.INVALID, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest("newFlwId", VerificationStatus.OTHERS.name(), "Out of town");

        frontLineWorkerService.updateVerifiedFlw(frontLineWorkerWebRequest);
    }

    @Test
    public void shouldAddNewLocationCorrespondingToFLWAndSaveToDb() {
        String name = "name";
        Designation designation = Designation.ANM;
        String flwId = "flwId";
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location("d", "b", "p", "VALID");
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, name, designation, location, flwId, VerificationStatus.OTHERS, "Random reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, VerificationStatus.SUCCESS.name(), name, designation.name(), new LocationRequest(district, block, panchayat)));

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(district, updatedFrontLineWorker.getLocation().getDistrict());
        assertEquals(block, updatedFrontLineWorker.getLocation().getBlock());
        assertEquals(panchayat,updatedFrontLineWorker.getLocation().getPanchayat());
        assertEquals("NOT VERIFIED", updatedFrontLineWorker.getLocation().getStatus());
    }
}