package org.motechproject.ananya.referencedata.contactCenter.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.mapper.LocationMapper;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private LocationService locationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UUID flwId = UUID.randomUUID();

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, locationService);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForUnsuccessfulRegistration() {
        String msisdn = "9988776655";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), "", Designation.ANM, new Location(), flwId, verificationStatus, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, msisdn, verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
        verify(locationService, never()).handleLocation(any(LocationRequest.class));
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForSuccessfulRegistration() {
        String msisdn = "9988776655";
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorker = new FrontLineWorker(Long.valueOf(msisdn), "", Designation.ANM, new Location(), flwId, verificationStatus, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.handleLocation(locationRequest)).thenReturn(existingLocation);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, msisdn, verificationStatus.name(), "name", Designation.ANM.name(), locationRequest));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation, actualFrontLineWorker.getLocation());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }

    @Test
    public void shouldCreateNewFLWIfFLWDoesNotExist() {
        String msisdn = "1122334455";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, msisdn, verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(Long.valueOf(msisdn), actualFrontLineWorker.getMsisdn());
        assertEquals(flwId, actualFrontLineWorker.getFlwId());
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        verify(locationService, never()).handleLocation(any(LocationRequest.class));
    }

    @Test
    public void shouldThrowExceptionIfRequestHasDifferentMsisdn() {
        Long existingMsisdn = 9988776655L;
        String newMsisdn = "1122334455";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(existingMsisdn, "", Designation.ANM, new Location(), flwId, verificationStatus, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Given msisdn %s does not match existing msisdn %s for the given id.", newMsisdn, existingMsisdn));
        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, newMsisdn, verificationStatus.name(), reason));

        verify(allFrontLineWorkers, never()).createOrUpdate(any(FrontLineWorker.class));
        verify(locationService, never()).handleLocation(any(LocationRequest.class));
    }
}