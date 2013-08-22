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
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
    public static final Long MSISDN = 9988776655L;
    public static final Long NEW_MSISDN = 9988776656L;
    public static final Long NEW_MSISDN_WITH_PREFIX = 919988776656L;
    public static final Long MSISDN_WITH_PREFIX = 919988776655L;
    public static final String NAME = "batman";

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerContactCenterService = new FrontLineWorkerContactCenterService(allFrontLineWorkers, locationService, requestValidator, syncService);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForUnsuccessfulRegistration() {
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String oldReason = "oldReason";
        String newReason = "newReason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, oldReason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN_WITH_PREFIX.toString(), verificationStatus.name(), newReason));

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
    public void alternativeContactNumberIsUseToCheckEquality() {
        FrontLineWorker flw1 = new FrontLineWorker();
        FrontLineWorker flw2 = new FrontLineWorker();
        flw2.setAlternateContactNumber(Long.MIN_VALUE);
        assertNotEquals(flw1, flw2);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForSuccessfulRegistration() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        String oldName = "batman";
        String newName = "spiderMan";

        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, oldName, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        FrontLineWorker unmodifiedMockFrontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, oldName, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker).thenReturn(unmodifiedMockFrontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        Location existingLocation = setUpLocationMock();

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), newName, Designation.ANM.name(), "district", "block", "panchy", null));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForDummyGuids() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorker1 = new FrontLineWorker(MSISDN_WITH_PREFIX, null, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        frontLineWorkers.add(frontLineWorker1);
        when(allFrontLineWorkers.getByMsisdn(MSISDN_WITH_PREFIX)).thenReturn(frontLineWorkers);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        Location existingLocation = setUpLocationMock();

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(UUID.fromString("11111111-1111-1111-1111-111111111111").toString(), MSISDN.toString(), verificationStatus.name(), "name", Designation.ANM.name(), "district", "block", "panchy", null));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker1.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerWithStatusWhenThereAreMultipleFlwForDummyGuids() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorkerWithStatus = new FrontLineWorker(MSISDN_WITH_PREFIX, null, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        FrontLineWorker frontLineWorkerWithoutStatus = new FrontLineWorker(MSISDN_WITH_PREFIX, "", Designation.ANM, new Location(), VerificationStatus.SUCCESS.name());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        frontLineWorkers.add(frontLineWorkerWithStatus);
        frontLineWorkers.add(frontLineWorkerWithoutStatus);
        when(allFrontLineWorkers.getByMsisdn(MSISDN_WITH_PREFIX)).thenReturn(frontLineWorkers);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        Location existingLocation = setUpLocationMock();

        frontLineWorkerContactCenterService.updateVerifiedFlw(successfulFrontLineWorkerVerificationWebRequest(UUID.fromString("11111111-1111-1111-1111-111111111111").toString(), MSISDN.toString(), verificationStatus.name(), "name", Designation.ANM.name(), "district", "block", "panchy", null));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorkerWithStatus.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldCreateNewFLWIfFLWDoesNotExist() {
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(flwId, actualFrontLineWorker.getFlwId());
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
        verify(syncService).syncFrontLineWorker(actualFrontLineWorker);
    }

    @Test
    public void shouldThrowExceptionIfRequestHasDifferentMsisdn() {
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", NEW_MSISDN_WITH_PREFIX, MSISDN_WITH_PREFIX));
        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), NEW_MSISDN_WITH_PREFIX.toString(), verificationStatus.name(), reason));

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldThrowExceptionIfRequestHasValidationErrors() {
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN, null, "", Designation.ANM, new Location(), verificationStatus.name(), flwId, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        Errors errors = new Errors();
        errors.add("some");
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(errors);
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("some");

        frontLineWorkerContactCenterService.updateVerifiedFlw(failedFrontLineWorkerVerificationWebRequest(flwId.toString(), NEW_MSISDN.toString(), verificationStatus.name(), reason));

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(locationService, never()).createAndFetch(any(LocationRequest.class));
    }

    @Test
    public void shouldNotMakeSyncRequestForExistingIdenticalFLW() {
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        Designation designation = Designation.ANM;
        Location location = new Location("d1", "b1", "p1", NAME, LocationStatus.VALID, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, designation, location, verificationStatus.name(), flwId, null);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        FrontLineWorkerVerificationWebRequest request = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN_WITH_PREFIX.toString(), verificationStatus.name(), NAME, designation.name(), location.getDistrict(), location.getBlock(), location.getPanchayat(), null);

        frontLineWorkerContactCenterService.updateVerifiedFlw(request);

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(syncService, never()).syncFrontLineWorker(any(FrontLineWorker.class));

    }

    @Test
    public void shouldUpdateFlwWithNewMSISDNWhenOnlyMsisdn() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;

        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        Location existingLocation = setUpLocationMock();
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(NEW_MSISDN.toString(), "");
        FrontLineWorkerVerificationWebRequest webRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), NAME, Designation.ANM.name(), "district", "block", "panchy", changeMsisdnRequest);

        frontLineWorkerContactCenterService.updateVerifiedFlw(webRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(NEW_MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldUpdateFlwWithNewMSISDNWhenDefaultFlwIdIsGivenAsARequest() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;

        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        Location existingLocation = setUpLocationMock();
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(NEW_MSISDN.toString(), FrontLineWorker.DEFAULT_UUID_STRING);
        FrontLineWorkerVerificationWebRequest webRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), NAME, Designation.ANM.name(), "district", "block", "panchy", changeMsisdnRequest);

        frontLineWorkerContactCenterService.updateVerifiedFlw(webRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(NEW_MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldUpdateFlwWithNewMSISDNAndRemoveExistingNewMsisdn() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;

        String newFlwId = UUID.randomUUID().toString();
        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, Designation.ANM, new Location(), verificationStatus.name(), flwId, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        Location existingLocation = setUpLocationMock();
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(NEW_MSISDN.toString(), newFlwId);
        FrontLineWorkerVerificationWebRequest webRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), NAME, Designation.ANM.name(), "district", "block", "panchy", changeMsisdnRequest);

        frontLineWorkerContactCenterService.updateVerifiedFlw(webRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        verify(allFrontLineWorkers).delete(newFlwId);
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(NEW_MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldCreateFlwWithNewMSISDNAndRemoveExistingNewMsisdn() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        String newFlwId = UUID.randomUUID().toString();

        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, Designation.ANM, new Location(), verificationStatus.name(), FrontLineWorker.DEFAULT_UUID, null);
        when(allFrontLineWorkers.getByMsisdn(MSISDN)).thenReturn(new ArrayList<FrontLineWorker>());
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        Location existingLocation = setUpLocationMock();
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(NEW_MSISDN.toString(), newFlwId);
        FrontLineWorkerVerificationWebRequest webRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), NAME, Designation.ANM.name(), "district", "block", "panchy", changeMsisdnRequest);

        frontLineWorkerContactCenterService.updateVerifiedFlw(webRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        verify(allFrontLineWorkers).delete(newFlwId);
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertNotEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(NEW_MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldCreateFlwWithNewMSISDNWhenFlwIdsAreDefaultGuids() {
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;

        FrontLineWorker frontLineWorker = new FrontLineWorker(MSISDN_WITH_PREFIX, null, NAME, Designation.ANM, new Location(), verificationStatus.name(), FrontLineWorker.DEFAULT_UUID, null);
        when(allFrontLineWorkers.getByMsisdn(MSISDN)).thenReturn(new ArrayList<FrontLineWorker>());
        when(requestValidator.validate(any(FrontLineWorkerVerificationRequest.class))).thenReturn(new Errors());
        Location existingLocation = setUpLocationMock();
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(NEW_MSISDN.toString(), FrontLineWorker.DEFAULT_UUID_STRING);
        FrontLineWorkerVerificationWebRequest webRequest = successfulFrontLineWorkerVerificationWebRequest(flwId.toString(), MSISDN.toString(), verificationStatus.name(), NAME, Designation.ANM.name(), "district", "block", "panchy", changeMsisdnRequest);

        frontLineWorkerContactCenterService.updateVerifiedFlw(webRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertNotEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(NEW_MSISDN_WITH_PREFIX, actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    private Location setUpLocationMock() {
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy", "state");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.createAndFetch(locationRequest)).thenReturn(existingLocation);
        return existingLocation;
    }

    private FrontLineWorkerVerificationWebRequest failedFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String reason) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus).withReason(reason);
        builder.withFailedVerification(true);
        return builder.build();
    }

    private FrontLineWorkerVerificationWebRequest successfulFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String name, String designation, String district, String block, String panchayat, ChangeMsisdnRequest changeMsisdnRequest) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus);
        builder.withName(name).withDesignation(designation).withDistrict(district).withBlock(block).withPanchayat(panchayat);
        builder.withChangeMsisdn(changeMsisdnRequest);
        return builder.build();
    }
}