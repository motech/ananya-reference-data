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
        expectedException.expectMessage("GUID is not present in MoTeCH");

        String guid = "11223344";
        when(allFrontLineWorkers.getByGuid(guid)).thenReturn(null);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(guid, "INVALID", "reason"));
    }

    @Test
    public void shouldUpdateExistingFrontLineWorker() {
        String guid = "11223344";
        String verificationStatus = "INVALID";
        String reason = "reason";
        FrontLineWorker frontLineWorker = new FrontLineWorker(9988776655L, "", Designation.ANM, new Location(), guid, verificationStatus, reason);

        when(allFrontLineWorkers.getByGuid(guid)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateVerifiedFlw(new FrontLineWorkerWebRequest(guid, verificationStatus, reason));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        FrontLineWorker actualFrontLineWorker = captor.getValue();
        assertEquals(verificationStatus, actualFrontLineWorker.getVerificationStatus());
        assertEquals(reason, actualFrontLineWorker.getReason());
        assertEquals(frontLineWorker.getFlwGuid(), actualFrontLineWorker.getFlwGuid());
        assertEquals(frontLineWorker.getMsisdn(), actualFrontLineWorker.getMsisdn());
    }
}
