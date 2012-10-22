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
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers);
    }

    @Test
    public void shouldValidateGivenFLWIfExistsInDatabase() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("FLW-Id is not present in MoTeCH");

        String flwId = "11223344";
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, VerificationStatus.INVALID.name(), "reason"));
    }

    @Test
    public void shouldUpdateExistingFrontLineWorker() {
        String flwId = "11223344";
        VerificationStatus verificationStatus = VerificationStatus.INVALID;
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(9988776655L, "", Designation.ANM, new Location(), flwId, verificationStatus, reason);
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(flwId, verificationStatus.name(), reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus.name(), actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getFlwId(), actualFrontLineWorker.getFlwId());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
    }
}