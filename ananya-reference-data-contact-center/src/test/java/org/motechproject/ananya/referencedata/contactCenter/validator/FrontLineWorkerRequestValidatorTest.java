package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerVerificationRequest;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.repository.AllFrontLineWorkers;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerRequestValidatorTest {
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    private FrontLineWorkerRequestValidator frontLineWorkerRequestValidator;

    @Before
    public void setUp() {
        frontLineWorkerRequestValidator = new FrontLineWorkerRequestValidator(allFrontLineWorkers);
    }

    @Test
    public void shouldInvalidateSuccessRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, null, null, null, null, null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(3, errors.getCount());
        assertEquals("designation field has invalid/blank value,name field has invalid/blank value,location is missing", errors.allMessages());
    }

    @Test
    public void shouldNotThrowValidationExceptionIfGuidIsSame() {
        final UUID flwId = UUID.randomUUID();
        final Long msisdn = 911234567890L;
        final String name = "name";
        final Designation anm = Designation.ANM;
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(msisdn, null, name, anm, new Location(), VerificationStatus.INVALID.name(), flwId, null));
        }};
        when(allFrontLineWorkers.getByMsisdnWithStatus(msisdn)).thenReturn(frontLineWorkers);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(flwId, PhoneNumber.formatPhoneNumber(msisdn.toString()), PhoneNumber.formatPhoneNumber(msisdn.toString()), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("D1", "B1", "P1", "state"), null, null);

        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);

        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldNotThrowValidationExceptionIfGuidIsDifferentButIncomingGuidIsDummy() {
        final UUID flwId = UUID.randomUUID();
        final Long msisdn = 911234567890L;
        final String name = "name";
        final Designation anm = Designation.ANM;
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(msisdn, null, name, anm, new Location(), VerificationStatus.INVALID.name(), flwId, null));
        }};
        when(allFrontLineWorkers.getByMsisdnWithStatus(msisdn)).thenReturn(frontLineWorkers);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.fromString("11111111-1111-1111-1111-111111111111"), PhoneNumber.formatPhoneNumber(msisdn.toString()), PhoneNumber.formatPhoneNumber(msisdn.toString()), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("D1", "B1", "P1", "state"), null, null);

        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);

        assertEquals(0, errors.getCount());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfNameIsInvalid() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "कुछ", Designation.ASHA, new LocationRequest("district", "block", "panchayat", "state"), null, null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(1, errors.getCount());
        assertEquals("name field has invalid/blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfLocationIsInvalid() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest(null, null, null, "state"), null, null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(3, errors.getCount());
        assertEquals("district field is blank,block field is blank,panchayat field is blank", errors.allMessages());
    }

    @Test
    public void shouldInvalidateSuccessRequestIfReasonIsFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), PhoneNumber.formatPhoneNumber("9900504646"), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("district", "block", "panchayat", "state"), "", null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(1, errors.getCount());
        assertEquals("reason field should not be a part of the request", errors.allMessages());
    }

    @Test
    public void shouldInvalidateInvalidRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), null, VerificationStatus.INVALID, null, null, null, null, null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(1, errors.getCount());
        assertEquals("reason field has blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateInvalidRequestIfExtraFieldsAreFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), Long.MIN_VALUE, VerificationStatus.INVALID, "name", Designation.ASHA, new LocationRequest(), "some reason", null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(3, errors.getCount());
        assertEquals("name field should not be a part of the request,location field should not be a part of the request,designation field should not be a part of the request", errors.allMessages());
    }

    @Test
    public void shouldInvalidateOtherRequestIfMandatoryFieldsAreMissing() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), null, VerificationStatus.OTHER, null, null, null, null, null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(1, errors.getCount());
        assertEquals("reason field has blank value", errors.allMessages());
    }

    @Test
    public void shouldInvalidateOtherRequestIfExtraFieldsAreFound() {
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber("9900504646"), null, VerificationStatus.OTHER, "name", Designation.ASHA, new LocationRequest(), "some reason", null);
        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);
        assertEquals(3, errors.getCount());
        assertEquals("name field should not be a part of the request,location field should not be a part of the request,designation field should not be a part of the request", errors.allMessages());
    }

    @Test
    public void shouldInvalidateIfAnotherFLWExistsWithSameMsisdnAndStatus() {
        final UUID flwId = UUID.randomUUID();
        final Long msisdn = 911234567890L;
        final String name = "name";
        final Designation anm = Designation.ANM;
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<FrontLineWorker>() {{
            add(new FrontLineWorker(msisdn, null, name, anm, new Location(), VerificationStatus.INVALID.name(), flwId, null));
        }};
        when(allFrontLineWorkers.getByMsisdnWithStatus(msisdn)).thenReturn(frontLineWorkers);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber(msisdn.toString()), PhoneNumber.formatPhoneNumber(msisdn.toString()), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("D1", "B1", "P1", "state"), null, null);

        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);

        assertEquals(1, errors.getCount());
        assertEquals("Conflicting flw record exists. Please try again later.", errors.allMessages());
    }

    @Test
    public void shouldInvalidateIfNewMsisdnFlwDoesNotExist(){
        final UUID flwId = UUID.randomUUID();
        final Long msisdn = 911234567890L;
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest("1231231231",flwId.toString());

        when(allFrontLineWorkers.getByMsisdnWithStatus(msisdn)).thenReturn(new ArrayList<FrontLineWorker>());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(null);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber(msisdn.toString()), PhoneNumber.formatPhoneNumber(msisdn.toString()), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("D1", "B1", "P1", "state"), null, changeMsisdnRequest);

        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);

        assertEquals(1, errors.getCount());
        assertEquals("NewMsisdn FrontLineWorker with given flwId not found", errors.allMessages());
    }

    @Test
    public void shouldInvalidateIfMsisdnDoesNotMatchWithNewMsisdnRequest(){
        final UUID flwId = UUID.randomUUID();
        final Long msisdn = 911234567890L;
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest("1231231231",flwId.toString());
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setMsisdn(912234567890L);

        when(allFrontLineWorkers.getByMsisdnWithStatus(msisdn)).thenReturn(new ArrayList<FrontLineWorker>());
        when(allFrontLineWorkers.getByFlwId(flwId)).thenReturn(frontLineWorker);
        FrontLineWorkerVerificationRequest verificationRequest = new FrontLineWorkerVerificationRequest(UUID.randomUUID(), PhoneNumber.formatPhoneNumber(msisdn.toString()), PhoneNumber.formatPhoneNumber(msisdn.toString()), VerificationStatus.SUCCESS, "name", Designation.ASHA, new LocationRequest("D1", "B1", "P1", "state"), null, changeMsisdnRequest);

        Errors errors = frontLineWorkerRequestValidator.validate(verificationRequest);

        assertEquals(1, errors.getCount());
        assertEquals("Msisdns do not match for FrontLineWorker of NewMsisdn request", errors.allMessages());
    }
}
