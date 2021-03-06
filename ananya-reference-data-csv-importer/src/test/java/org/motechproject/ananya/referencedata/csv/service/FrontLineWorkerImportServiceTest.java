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
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFlwUuid;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.repository.AllUploadFlwMetaData;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.service.SyncService;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerImportServiceTest {

    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private SyncService syncService;
    @Mock
    private Properties clientServicesProperties;
    @Mock
    private AllUploadFlwMetaData allUploadFlwMetaData;
    @Mock
    private AllFlwUuid allFlwUuid;
    @Captor
    ArgumentCaptor<List<FrontLineWorker>> captor;
    @Captor
    ArgumentCaptor<List<FrontLineWorker>> anotherCaptor;

    private FrontLineWorkerImportService frontLineWorkerImportService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImportService = new FrontLineWorkerImportService(allLocations, 
        		allFrontLineWorkers, 
        		syncService,allUploadFlwMetaData,allFlwUuid);
    }

    @Test
    public void shouldAddFLWsInBulk() throws Exception {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        String msisdn1 = "911234454522";
        String msisdn2 = "911234454623";
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn1, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerImportRequest2 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn2, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.VALID, null));
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.VALID, null));

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
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn1, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.VALID, null));
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
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "VALID"));
        FrontLineWorkerImportRequest frontLineWorkerImportRequest2 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "VALID"));
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.VALID, null));
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
        FrontLineWorkerImportRequest frontLineWorkerImportRequest1 = new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest(district, block, panchayat, "state", "INVALID"));
        Location validLocation = new Location("state", "d1", "b1", "p1", LocationStatus.VALID, null);
        when(allLocations.getFor("state", district, block, panchayat)).thenReturn(new Location("state", district, block, panchayat, LocationStatus.INVALID, validLocation));
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

    @Test
    public void shouldMapToExistingFLWWithVerificationStatusIncaseOfDuplicates() {
        String msisdn = "1234567890";
        FrontLineWorkerImportRequest request = new FrontLineWorkerImportRequest(null, msisdn, "1234567891", "name", "ASHA", VerificationStatus.SUCCESS.name(), new LocationRequest());
        UUID flwId = UUID.randomUUID();
        FrontLineWorker frontLineWorkerWithStatus = new FrontLineWorker(null, null, null, null, null, VerificationStatus.INVALID.name(), flwId,null);
        FrontLineWorker otherFrontLineWorker = new FrontLineWorker();
        when(allFrontLineWorkers.getByMsisdn(PhoneNumber.formatPhoneNumber(msisdn))).thenReturn(asList(otherFrontLineWorker, frontLineWorkerWithStatus));
        when(allLocations.getFor(anyString(),anyString(),anyString(),anyString())).thenReturn(new Location());

        frontLineWorkerImportService.addAllWithoutValidations(asList(request));

        verify(allFrontLineWorkers).createOrUpdateAll(captor.capture());
        List<FrontLineWorker> actualFlws = captor.getValue();
        assertEquals(1, actualFlws.size());
        assertEquals(flwId, actualFlws.get(0).getFlwId());
    }
}
