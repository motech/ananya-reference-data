package org.motechproject.ananya.referencedata.flw.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.service.FrontLineWorkerService;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerSyncEventHandlerTest {
    @Mock
    FrontLineWorkerService frontLineWorkerService;
    @Mock
    SyncService syncService;

    @Test
    public void shouldPutFLWItemsWhichHaveBeenCreatedOrUpdatedInTheQueue() {
        FrontLineWorkerSyncEventHandler frontLineWorkerSyncEventHandler = new FrontLineWorkerSyncEventHandler(frontLineWorkerService, syncService);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>();
        Long msisdn = 1234567890L;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, new Location("district", "block", "panchayat"));
        frontLineWorker.setShouldSync(true);
        frontLineWorkers.add(frontLineWorker);
        when(frontLineWorkerService.getAllToBeSynced()).thenReturn(frontLineWorkers);

        frontLineWorkerSyncEventHandler.scheduleSync(null);

        verify(syncService).syncFrontLineWorker(msisdn);
        verify(frontLineWorkerService).setSyncComplete(frontLineWorker);
   }

}
