package org.motechproject.ananya.referencedata.flw.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Captor
    ArgumentCaptor<List<FrontLineWorker>> captor;

    private FrontLineWorkerService frontLineWorkerService;

    @Before
    public void setUp(){
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers);
    }

    @Test
    public void shouldUpdateLocationsOfAllFLWsFromCurrentLocationToAlternateLocation() {
        Location alternateLocation = new Location("state", "district", "block", "panchayat", LocationStatus.VALID, null);;
        Location currentLocation = new Location("state", "district", "block", "panchayat", LocationStatus.NOT_VERIFIED, alternateLocation);
        List<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        FrontLineWorker frontLineWorker = new FrontLineWorker(1234567890L, "name", Designation.ASHA, currentLocation, VerificationStatus.SUCCESS.name());
        frontLineWorkers.add(frontLineWorker);
        when(allFrontLineWorkers.getForLocation(currentLocation)).thenReturn(frontLineWorkers);

        frontLineWorkerService.updateWithAlternateLocationForFLWsWith(currentLocation);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFrontLineWorkers = captor.getValue();
        assertEquals(alternateLocation, actualFrontLineWorkers.get(0).getLocation());
    }
}