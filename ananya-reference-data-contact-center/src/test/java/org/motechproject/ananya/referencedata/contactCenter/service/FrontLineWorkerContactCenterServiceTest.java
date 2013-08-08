package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequestBuilder;
import org.motechproject.ananya.referencedata.contactCenter.validator.FrontLineWorkerRequestValidator;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerContactCenterServiceTest {
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private LocationService locationService;
    @Mock
    private FrontLineWorkerRequestValidator requestValidator;
    @Mock
    private SyncService syncService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private UUID flwId = UUID.randomUUID();

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerContactCenterService = new FrontLineWorkerContactCenterService(allFrontLineWorkers, locationService, requestValidator, syncService);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForUnsuccessfulRegistration() {
        String msisdn = "919988776655";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String oldReason = "oldReason";
        String newReason = "newReason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), "", Designation.ANM, new Location(), verificationStatus.name(), flwId, oldReason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn, verificationStatus.name(), newReason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(newReason, actualFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForSuccessfulRegistration() {
        Long msisdn = 9988776655L;
        Long msisdnWithPrefix = 919988776655L;
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        String oldName = "batman";
        String newName = "spiderMan";

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdnWithPrefix, oldName, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        FrontLineWorker unmodifiedMockFrontLineWorker = new FrontLineWorker(msisdnWithPrefix, oldName, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker).thenReturn(unmodifiedMockFrontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy", "state");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.createAndFetch(locationRequest)).thenReturn(existingLocation);

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn.toString(), verificationStatus.name(), newName, Designation.ANM.name(), "district", "block", "panchy"));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(msisdnWithPrefix, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForDummyGuids() {
        Long msisdn = 9988776655L;
        Long msisdnWithPrefix = 919988776655L;

        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(msisdnWithPrefix, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        frontLineWorkers.add(frontLineWorker1);
        when(allFrontLineWorkers.getByMsisdn(msisdnWithPrefix)).thenReturn(frontLineWorkers);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy", "state");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.createAndFetch(locationRequest)).thenReturn(existingLocation);

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(UUID.fromString("11111111-1111-1111-1111-111111111111").toString(), msisdn.toString(), verificationStatus.name(), "name", Designation.ANM.name(), "district", "block", "panchy"));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker1.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(msisdnWithPrefix, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerWithStatusWhenThereAreMultipleFlwForDummyGuids() {
        Long msisdn = 9988776655L;
        Long msisdnWithPrefix = 919988776655L;

        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorkerWithStatus = new FrontLineWorker(msisdnWithPrefix, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        FrontLineWorker frontLineWorkerWithoutStatus = new FrontLineWorker(msisdnWithPrefix, "", Designation.ANM, new Location(), VerificationStatus.SUCCESS.name());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        frontLineWorkers.add(frontLineWorkerWithStatus);
        frontLineWorkers.add(frontLineWorkerWithoutStatus);
        when(allFrontLineWorkers.getByMsisdn(msisdnWithPrefix)).thenReturn(frontLineWorkers);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy", "state");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.createAndFetch(locationRequest)).thenReturn(existingLocation);

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(UUID.fromString("11111111-1111-1111-1111-111111111111").toString(), msisdn.toString(), verificationStatus.name(), "name", Designation.ANM.name(), "district", "block", "panchy"));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorkerWithStatus.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(msisdnWithPrefix, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldCreateNewFLWIfFLWDoesNotExist() {
        Long msisdn = 1122334455L;
        Long msisdnWithPrefix = 911122334455L;
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), msisdn.toString(), verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(msisdnWithPrefix, actualFrontLineWorker.getMsisdn());
        assertEquals(flwId, actualFrontLineWorker.getFlwId());
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldThrowExceptionIfRequestHasDifferentMsisdn() {
        Long existingMsisdn = 919988776655L;
        String newMsisdn = "911122334455";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(existingMsisdn, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", newMsisdn, existingMsisdn));
        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), newMsisdn, verificationStatus.name(), reason));

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldThrowExceptionIfRequestHasValidationErrors() {
        Long existingMsisdn = 919988776655L;
        String newMsisdn = "911122334455";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(existingMsisdn, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        Errors errors = new Errors();
        errors.add("some");
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(errors);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("some");

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), newMsisdn, verificationStatus.name(), reason));

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldNotMakeSyncRequestForExistingIdenticalFLW(){
        Long existingMsisdn = 919988776655L;
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String name = "aragorn";
        Designation designation = Designation.ANM;
        Location location = new Location(name, "d1","b1","p1", LocationStatus.VALID,null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(existingMsisdn, name, designation, location, verificationStatus.name(), flwId, null);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        FrontLineWorkerVerificationWebRequest request = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), existingMsisdn.toString(), verificationStatus.name(),name,designation.name(),location.getDistrict(),location.getBlock(),location.getPanchayat());

        frontLineWorkerContactCenterService.updateVerifiedFlw(request);

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(syncService, never()).syncFrontLineWorker(any(FrontLineWorker.class));

    }

    private FrontLineWorkerVerificationWebRequest failedFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String reason) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus).withReason(reason);
        builder.withFailedVerification(true);
        return builder.build();
    }

    public FrontLineWorkerVerificationWebRequest successfulFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String name, String designation, String district, String block, String panchayat) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus);
        builder.withName(name).withDesignation(designation).withDistrict(district).withBlock(block).withPanchayat(panchayat);
        return builder.build();
    }
}