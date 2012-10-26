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
    private UUID flwId = UUID.randomUUID();

    @Test
    public void shouldUpdateAnExistingFlwDuringUnsuccessfulRegistration() {
        String msisdn = "1234567890";
        Location location = new Location("d", "b", "p", "VALID");
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.parseLong(msisdn), null, null, location, flwId, VerificationStatus.INVALID, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId.toString(), msisdn, VerificationStatus.OTHER.name(), "Out of town");

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
    public void shouldCreateANewFlwIfFLWDoesNotExistDuringRegistration() {
        String msisdn = "1234567890";

        Location location = new Location("d", "b", "p", "VALID");
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), "name", Designation.ANM, location, UUID.randomUUID(), VerificationStatus.INVALID, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId.toString(), msisdn, VerificationStatus.OTHER.name(), "Out of town");

        frontLineWorkerService.updateVerifiedFlw(frontLineWorkerWebRequest);

        assertEquals(2, template.loadAll(FrontLineWorker.class).size());
    }

    @Test
    public void shouldUpdateAnExistingFlwDuringSuccessfulRegistration() {
        String msisdn = "1234567890";
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), "Shahrukh", null, null, flwId, VerificationStatus.INVALID, "reason");
        String name = "New Name";
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        Location location = LocationMapper.mapFrom(locationRequest);
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        template.flush();
        String designation = Designation.ANM.name();
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId.toString(), msisdn, VerificationStatus.SUCCESS.name(), name, designation, locationRequest);

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
    public void shouldAddNewLocationCorrespondingToFLWAndSaveToDb() {
        String name = "name";
        String msisdn = "1234567890";
        Designation designation = Designation.ANM;
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        Location location = new Location("d", "b", "p", "VALID");
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), name, designation, location, flwId, VerificationStatus.OTHER, "Random reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId.toString(), msisdn, VerificationStatus.SUCCESS.name(), name, designation.name(), new LocationRequest(district, block, panchayat)));

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(district, updatedFrontLineWorker.getLocation().getDistrict());
        assertEquals(block, updatedFrontLineWorker.getLocation().getBlock());
        assertEquals(panchayat,updatedFrontLineWorker.getLocation().getPanchayat());
        assertEquals("NOT_VERIFIED", updatedFrontLineWorker.getLocation().getStatus());
    }
}