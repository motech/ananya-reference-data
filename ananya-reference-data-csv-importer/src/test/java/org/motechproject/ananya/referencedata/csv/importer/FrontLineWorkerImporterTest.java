package org.motechproject.ananya.referencedata.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.csv.request.FrontLineWorkerImportRequest;
import org.motechproject.ananya.referencedata.csv.service.FrontLineWorkerImportService;
import org.motechproject.ananya.referencedata.csv.service.LocationImportService;
import org.motechproject.ananya.referencedata.flw.domain.*;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.importer.domain.ValidationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerImporterTest {
    @Mock
    private LocationImportService locationImportService;
    @Mock
    private FrontLineWorkerImportService frontLineWorkerImportService;
    @Mock
    private Properties clientServicesProperties;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    private FrontLineWorkerImporter frontLineWorkerImporter;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerImporter = new FrontLineWorkerImporter(frontLineWorkerImportService, locationImportService,allFrontLineWorkers);
    }

    @Test
    public void shouldValidateFLWRequests() {
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        Location location = new Location("D1", "B1", "P1", "state", LocationStatus.VALID, null);
        UUID uuid = UUID.randomUUID();
        when(locationImportService.getFor("state", "D1", "B1", "P1")).thenReturn(location);
        when(allFrontLineWorkers.getByFlwId(uuid)).thenReturn(new FrontLineWorker(PhoneNumber.formatPhoneNumber("1234567890"),"name",Designation.ANM, null, null));
        frontLineWorkerWebRequests.add(new FrontLineWorkerImportRequest(uuid.toString(), "1234567890", "1234567891", "name", Designation.ANM.name(), VerificationStatus.SUCCESS.name(), new LocationRequest("D1", "B1", "P1", "state", "VALID")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerWebRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat,error", validationResponse.getErrors().get(0).getMessage());
        verify(locationImportService).invalidateCache();
    }

    @Test
    public void shouldFailValidationIfFLWDoesNotHaveAllTheDetails() {
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        Location location = new Location("D1", "B1", "P1", "state", LocationStatus.VALID, null);
        when(locationImportService.getFor("state", "D1", "B1", "P1")).thenReturn(location);
        UUID uuid = UUID.randomUUID();
        when(allFrontLineWorkers.getByFlwId(uuid)).thenReturn(new FrontLineWorker(PhoneNumber.formatPhoneNumber("1234567890"),"name",Designation.ANM, null, null));

        frontLineWorkerWebRequests.add(new FrontLineWorkerImportRequest(uuid.toString(), "1asdf67890", "1234567891", "name", Designation.ANM.name(), VerificationStatus.SUCCESS.name(), new LocationRequest("D1", "B1", "P1", "state", "VALID")));

        ValidationResponse validationResponse = frontLineWorkerImporter.validate(frontLineWorkerWebRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(2, validationResponse.getErrors().size());
        assertEquals("\"1asdf67890\",\"name\",\"ANM\",\"state\",\"D1\",\"B1\",\"P1\",\"[Invalid msisdn]\"", validationResponse.getErrors().get(1).getMessage());
        verify(locationImportService).invalidateCache();
    }

    @Test
    public void shouldSaveFLW() throws IOException {
        ArrayList<FrontLineWorkerImportRequest> frontLineWorkerWebRequests = new ArrayList<>();
        String msisdn = "1234567890";
        frontLineWorkerWebRequests.add(new FrontLineWorkerImportRequest(UUID.randomUUID().toString(), msisdn, "1234567891", "name", Designation.ANM.name(), VerificationStatus.SUCCESS.name(), new LocationRequest("D1", "B1", "P1", "state", "VALID")));

        frontLineWorkerImporter.postData(frontLineWorkerWebRequests);

        verify(frontLineWorkerImportService).addAllWithoutValidations(frontLineWorkerWebRequests);
    }
}
