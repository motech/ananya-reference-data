package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FrontLineWorkerImportServiceTest {

    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private SyncService syncService;
    @Mock
    private Properties clientServicesProperties;

    @Captor
    ArgumentCaptor<List<FrontLineWorker>> captor;
    @Captor
    ArgumentCaptor<List<FrontLineWorker>> anotherCaptor;

    private FrontLineWorkerImportService frontLineWorkerImportService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImportService = new FrontLineWorkerImportService(allLocations, allFrontLineWorkers, syncService);
    }

    @Test
    public void shouldAddFLWsInBulk() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454522";
        String msisdn2 = "911234454623";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerImportRequest2 = new FrontLineWorkerImportRequest(msisdn2, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "state", LocationStatus.VALID, null));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "state", LocationStatus.VALID, null));

        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerImportRequests = new ArrayList<>();
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest1);
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest2);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerImportRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> frontLineWorkers = captor.getValue();
        assertEquals(msisdn1, frontLineWorkers.get(0).getMsisdn().toString());
        assertEquals(msisdn2, frontLineWorkers.get(1).getMsisdn().toString());
    }

    @Test
    public void shouldUpdateFLWInBulkIfOnlyOneFLWIsPresentInDBAndOnlyOneFLWIsPresentInTheCSVWithThatMSISDN() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454545";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(msisdn1, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "state", LocationStatus.VALID, null));
        FrontLineWorker expectedFLW = new FrontLineWorker();
        List<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        frontLineWorkers.add(expectedFLW);
        when(allFrontLineWorkers.getByMsisdn(Long.parseLong(msisdn1))).thenReturn(frontLineWorkers);
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerImportRequests = new ArrayList<>();
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest1);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerImportRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertTrue(actualFLWs.contains(expectedFLW));
        assertEquals(1, actualFLWs.size());
        verify(syncService).syncAllFrontLineWorkers(anotherCaptor.capture());
        List<FrontLineWorker> actualFLWsForSync = anotherCaptor.getValue();
        assertEquals(actualFLWsForSync, actualFLWs);
    }

    @Test
    public void shouldAddRecordsAndNotUpdateIfThereAreDuplicatesWithinTheRequestList() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn = "12344545";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(msisdn, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerImportRequest2 = new FrontLineWorkerImportRequest(msisdn, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "state", LocationStatus.VALID, null));
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerImportRequests = new ArrayList<>();
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest1);
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest2);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerImportRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertEquals(2, actualFLWs.size());
    }

    @Test
    public void shouldAddRecordsWithValidLocationIfTheyComeInWithInvalidLocation() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn = "12344545";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(msisdn, "name", "ASHA", new LocationRequest(district, block, panchayat, "state", "INVALID"));
        Location validLocation = new Location("d1", "b1", "p1", "state", LocationStatus.VALID, null);
        when(allLocations.getFor(district, block, panchayat)).thenReturn(new Location(district, block, panchayat, "state", LocationStatus.INVALID, validLocation));
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerImportRequests = new ArrayList<>();
        frontLineWorkerImportRequests.add(frontLineWorkerImportRequest1);

        frontLineWorkerImportService.addAllWithoutValidations(frontLineWorkerImportRequests);

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFLWs = captor.getValue();
        assertEquals(1, actualFLWs.size());
        assertEquals(validLocation, actualFLWs.get(0).getLocation());
    }

    @Test
    public void shouldGetFrontLineWorkerById() {
        Long msisdn = 12L;

        frontLineWorkerImportService.getAllByMsisdn(msisdn);

        verify(allFrontLineWorkers).getByMsisdn(msisdn);
    }
}