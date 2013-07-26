package org.motechproject.ananya.referencedata.contactCenter.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.referencedata.contactCenter.SpringIntegrationTest;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequestBuilder;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang.WordUtils.capitalizeFully;

public class FrontLineWorkerContectCenterServiceIT extends SpringIntegrationTest {

    @Autowired
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private AllLocations allLocations;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private UUID flwId = UUID.randomUUID();

    @Test
    public void shouldUpdateAnExistingFlwDuringUnsuccessfulRegistration() {
        String msisdn = "911234567890";
        Location location = new Location("d", "b", "p", msisdn, LocationStatus.VALID, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.parseLong(msisdn), null, null, null, location, VerificationStatus.INVALID.name(), flwId, "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        String newReason = "Out of town";
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn, VerificationStatus.OTHER.name(), newReason);

        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(VerificationStatus.OTHER.name(), updatedFrontLineWorker.getVerificationStatus());
        assertEquals(newReason, updatedFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getName(), updatedFrontLineWorker.getName());
        assertEquals(frontLineWorker.getMsisdn(), updatedFrontLineWorker.getMsisdn());
        assertEquals(frontLineWorker.getDesignation(), updatedFrontLineWorker.getDesignation());
        assertEquals(frontLineWorker.getLocation(), updatedFrontLineWorker.getLocation());
    }

    @Test
    public void shouldCreateANewFlwIfFLWDoesNotExistDuringRegistration() {
        String msisdn = "1234567890";

        Location location = new Location("d", "b", "p", msisdn, LocationStatus.VALID, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), null, "name", Designation.ANM, location, VerificationStatus.INVALID.name(), UUID.randomUUID(), "reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn, VerificationStatus.OTHER.name(), "Out of town");

        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);

        assertEquals(2, template.loadAll(FrontLineWorker.class).size());
    }

    @Test
    public void shouldUpdateAnExistingFlwDuringSuccessfulRegistration() {
        String msisdn = "911234567890";
        String alternateContactNumber = "911234567899";
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), Long.valueOf(alternateContactNumber), "Shahrukh", null, null, VerificationStatus.INVALID.name(), flwId, "reason");
        String name = "New Name";
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat", "state");
        Location location = LocationMapper.mapFrom(locationRequest);
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);
        template.flush();
        String designation = Designation.ANM.name();
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn, VerificationStatus.SUCCESS.name(), name, designation, "district", "block", "panchayat", alternateContactNumber);

        frontLineWorkerContactCenterService.updateVerifiedFlw(frontLineWorkerWebRequest);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(frontLineWorker.getFlwId(), updatedFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), updatedFrontLineWorker.getMsisdn());
        assertEquals(frontLineWorker.getAlternateContactNumber(), updatedFrontLineWorker.getAlternateContactNumber());
        assertEquals(VerificationStatus.SUCCESS.name(), updatedFrontLineWorker.getVerificationStatus());
        assertEquals(name, updatedFrontLineWorker.getName());
        assertEquals(designation, updatedFrontLineWorker.getDesignation());
        assertEquals(location, updatedFrontLineWorker.getLocation());
        assertEquals(null, updatedFrontLineWorker.getReason());
    }

    @Test
    public void shouldAddNewLocationCorrespondingToFLWAndSaveToDb() {
        String name = "name";
        String msisdn = "911234567890";
        String alternateContactNumber = "911234567899";
        Designation designation = Designation.ANM;
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String state = "state";
        Location location = new Location("d", "b", "p", state, LocationStatus.VALID, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), null, name, designation, location, VerificationStatus.OTHER.name(), flwId, "Random reason");
        allLocations.add(location);
        allFrontLineWorkers.add(frontLineWorker);

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn, VerificationStatus.SUCCESS.name(), name, designation.name(), district, block, panchayat, alternateContactNumber));

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.getByFlwId(flwId);
        assertEquals(alternateContactNumber, updatedFrontLineWorker.getAlternateContactNumber().toString());
        assertEquals(capitalizeFully(district), updatedFrontLineWorker.getLocation().getDistrict());
        assertEquals(capitalizeFully(block), updatedFrontLineWorker.getLocation().getBlock());
        assertEquals(capitalizeFully(panchayat), updatedFrontLineWorker.getLocation().getPanchayat());
        assertEquals(capitalizeFully(state), updatedFrontLineWorker.getLocation().getState());
        assertEquals(LocationStatus.NOT_VERIFIED, updatedFrontLineWorker.getLocation().getStatus());
    }

    private FrontLineWorkerVerificationWebRequest failedFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String reason) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus).withReason(reason);
        builder.withFailedVerification(true);
        return builder.build();
    }

    public FrontLineWorkerVerificationWebRequest successfulFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String name, String designation, String district, String block, String panchayat, String alternateContactNumber) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus);
        builder.withName(name).withDesignation(designation).withDistrict(district).withBlock(block).withPanchayat(panchayat)
        .withAlternateContactNumber(alternateContactNumber);
        return builder.build();
    }
}