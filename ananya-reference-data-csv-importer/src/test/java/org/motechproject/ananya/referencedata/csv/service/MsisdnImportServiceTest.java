package org.motechproject.ananya.referencedata.csv.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.csv.request.MsisdnImportRequest;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MsisdnImportServiceTest {

    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Captor
    private ArgumentCaptor<List<FrontLineWorker>> frontLineWorkerCaptor;

    private MsisdnImportService msisdnImportService;

    @Before
    public void setUp() throws Exception {
        msisdnImportService = new MsisdnImportService(allFrontLineWorkers);
    }

    @Test
    public void shouldDeleteFLWByNewMsisdnAndUpdateFLWHavingOldMsisdnWithNewMsisdn() {
        String msisdn1 = "1234567890";
        String newMsisdn1 = "1234567891";
        MsisdnImportRequest msisdnImportRequest1 = setUpRequest(msisdn1, newMsisdn1, null);
        String msisdn2 = "9876543210";
        String newMsisdn2 = "9876543211";
        MsisdnImportRequest msisdnImportRequest2 = setUpRequest(msisdn2, newMsisdn2, null);
        Long newMsisdn1AsLong = PhoneNumber.formatPhoneNumber(newMsisdn1);
        Long newMsisdn2AsLong = PhoneNumber.formatPhoneNumber(newMsisdn2);

        msisdnImportService.updateFLWContactDetailsWithoutValidations(asList(msisdnImportRequest1, msisdnImportRequest2));

        ArgumentCaptor<Long> newMsisdnCaptor = ArgumentCaptor.forClass(Long.class);
        verify(allFrontLineWorkers, times(2)).deleteByMsisdn(newMsisdnCaptor.capture());
        List<Long> deletedMsisdns = newMsisdnCaptor.getAllValues();
        assertEquals(2, deletedMsisdns.size());
        assertEquals(newMsisdn1AsLong, deletedMsisdns.get(0));
        assertEquals(newMsisdn2AsLong, deletedMsisdns.get(1));

        verify(allFrontLineWorkers).createOrUpdateAll(frontLineWorkerCaptor.capture());
        List<FrontLineWorker> updatedFLWs = frontLineWorkerCaptor.getValue();
        assertEquals(2, updatedFLWs.size());
        assertEquals(newMsisdn1AsLong, updatedFLWs.get(0).getMsisdn());
        assertEquals(newMsisdn2AsLong, updatedFLWs.get(1).getMsisdn());
    }

    @Test
    public void shouldNotChangeMsisdnOfFLWIfRequestIsNotForChangeMsisdn() {
        String msisdn = "1234567890";
        MsisdnImportRequest msisdnImportRequest = setUpRequest(msisdn, "", "1234567891");

        msisdnImportService.updateFLWContactDetailsWithoutValidations(asList(msisdnImportRequest));

        verify(allFrontLineWorkers, never()).deleteByMsisdn(anyLong());

        verify(allFrontLineWorkers).createOrUpdateAll(frontLineWorkerCaptor.capture());
        List<FrontLineWorker> actualFLWs = frontLineWorkerCaptor.getValue();
        assertEquals(1, actualFLWs.size());
        assertEquals(PhoneNumber.formatPhoneNumber(msisdn), actualFLWs.get(0).getMsisdn());
    }

    @Test
    public void shouldUpdateAlternateContactNumberOfFLW() {
        String alternateContactNumber1 = "1234567891";
        MsisdnImportRequest msisdnImportRequest1 = setUpRequest("1234567890", null, alternateContactNumber1);
        String alternateContactNumber2 = "9876543212";
        MsisdnImportRequest msisdnImportRequest2 = setUpRequest("9876543210", "9876543211", alternateContactNumber2);
        Long formattedAlternateContactNumber1 = PhoneNumber.formatPhoneNumber(alternateContactNumber1);
        Long formattedAlternateContactNumber2 = PhoneNumber.formatPhoneNumber(alternateContactNumber2);

        msisdnImportService.updateFLWContactDetailsWithoutValidations(asList(msisdnImportRequest1, msisdnImportRequest2));

        verify(allFrontLineWorkers).createOrUpdateAll(frontLineWorkerCaptor.capture());
        List<FrontLineWorker> updatedFLWs = frontLineWorkerCaptor.getValue();
        assertEquals(2, updatedFLWs.size());
        assertEquals(formattedAlternateContactNumber1, updatedFLWs.get(0).getAlternateContactNumber());
        assertEquals(formattedAlternateContactNumber2, updatedFLWs.get(1).getAlternateContactNumber());
    }

    @Test
    public void shouldNotUpdateAlternateContactNumberOfFLWIfRequestDoesNotHaveIt() {
        String msisdn1 = "1234567890";
        String msisdn2 = "9876543210";
        MsisdnImportRequest msisdnImportRequest1 = setUpRequest(msisdn1, "1234567891", "");
        MsisdnImportRequest msisdnImportRequest2 = setUpRequest(msisdn2, "9876543211", null);

        msisdnImportService.updateFLWContactDetailsWithoutValidations(asList(msisdnImportRequest1, msisdnImportRequest2));

        verify(allFrontLineWorkers).createOrUpdateAll(frontLineWorkerCaptor.capture());
        List<FrontLineWorker> actualFLWs = frontLineWorkerCaptor.getValue();
        assertEquals(2, actualFLWs.size());
        assertNull(actualFLWs.get(0).getAlternateContactNumber());
        assertNull(actualFLWs.get(1).getAlternateContactNumber());
    }

    private MsisdnImportRequest setUpRequest(String msisdn, String newMsisdn, String alternateContactNumber) {
        Long msisdnAsLong = PhoneNumber.formatPhoneNumber(msisdn);
        MsisdnImportRequest msisdnImportRequest = new MsisdnImportRequest(msisdn, newMsisdn, alternateContactNumber);
        FrontLineWorker frontLineWorkerByMsisdn = new FrontLineWorker(UUID.randomUUID());
        frontLineWorkerByMsisdn.setMsisdn(msisdnAsLong);
        when(allFrontLineWorkers.getByMsisdn(msisdnAsLong)).thenReturn(asList(frontLineWorkerByMsisdn));

        return msisdnImportRequest;
    }
}
