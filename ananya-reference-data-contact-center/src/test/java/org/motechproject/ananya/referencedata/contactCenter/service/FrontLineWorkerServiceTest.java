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

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, locationService);
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForUnsuccessfulRegistration() {
        String flwId = "11223344";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(9988776655L, "", Designation.ANM, new Location(), flwId, verificationStatus, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
        verify(locationService,never()).handleLocation(any(LocationRequest.class));
    }

    @Test
    public void shouldUpdateExistingFrontLineWorkerForSuccessfulRegistration() {
        String flwId = "11223344";
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        FrontLineWorker frontLineWorker = new FrontLineWorker(9988776655L, "", Designation.ANM, new Location(), flwId, verificationStatus, null);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        LocationRequest locationRequest = new LocationRequest("district", "block", "panchy");
        Location existingLocation = LocationMapper.mapFrom(locationRequest);
        when(locationService.handleLocation(locationRequest)).thenReturn(existingLocation);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, verificationStatus.name(),"name",Designation.ANM.name(), locationRequest));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).createOrUpdate(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(existingLocation,actualFrontLineWorker.getLocation());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
        assertEquals(null, actualFrontLineWorker.getReason());
    }
}