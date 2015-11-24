package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerContactCenterService;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.service.DefaultRequestValues;
import org.motechproject.ananya.referencedata.web.utils.FrontLineWorkerVerificationWebRequestBuilder;
import org.motechproject.ananya.referencedata.web.utils.TestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class FrontLineWorkerControllerTest {

    private static final String DEFAULT_STATE = "Bihar";
    @Mock
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;


    private FrontLineWorkerController frontLineWorkerController;
    private String flwId = UUID.randomUUID().toString();
    private String channel = "contact_center";
    private DefaultRequestValues defaultRequestValues;
    @Mock
    private LocationService locationService;
    @Before
    public void setUp() {
        initMocks(this);

        defaultRequestValues = new DefaultRequestValues(DEFAULT_STATE);
        frontLineWorkerController = new FrontLineWorkerController(frontLineWorkerContactCenterService,defaultRequestValues,locationService);
    }

    @Test
    public void shouldUpdateTheStatusForAValidFLWRequestXml() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.INVALID.name(), "Invalid User");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.success("The FLW has been updated successfully"), status().isOk(), channel);

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldInvalidateTheRequestIfMsisdnIsNot10DigitNumber() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId, "911234567890", VerificationStatus.INVALID.name(), "Invalid User");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.failure("msisdn field has invalid value"), status().isBadRequest(), channel);

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerVerificationWebRequest.class));
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidFLWRequestXml() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId, "1234567890", null, "Invalid User");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.failure("verificationStatus field is missing"), status().isBadRequest(), "contact_center");

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerVerificationWebRequest.class));
    }

    @Test
    public void shouldUpdateTheStatusForAValidFLWRequestJson() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.INVALID.name(), "Invalid User");

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.success("The FLW has been updated successfully"), status().isOk());

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidFLWRequestJson() throws Exception {
        String reason = "Invalid User";
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = failedFrontLineWorkerVerificationWebRequest(flwId, "1234567890", null, reason);

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.failure("verificationStatus field is missing"), status().isBadRequest());

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerVerificationWebRequest.class));
    }

    @Test
    public void shouldUpdateTheStatusForAValidSuccessfulFLWRequestJson() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), "district", "block", "panchayat", "state");

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.success("The FLW has been updated successfully"), status().isOk());

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidSuccessfulFLWRequestJson() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), null, null, null, null);
        FrontLineWorkerVerificationWebRequest expectedFrontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), null, null, null, DEFAULT_STATE);

        BaseResponse expectedResponse = BaseResponse.failure("some validation failed");
        doThrow(new ValidationException("some validation failed")).when(frontLineWorkerContactCenterService).updateVerifiedFlw(expectedFrontLineWorkerWebRequest);

        postFlwRequestJson(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest());

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(expectedFrontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldUpdateTheStatusForAValidSuccessfulFLWRequestXml() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), "district", "block", "panchayat", "state");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.success("The FLW has been updated successfully"), status().isOk(), "contact_center");

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidSuccessfulFLWRequestXml() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), null, null, null, null);
        FrontLineWorkerVerificationWebRequest expectedFrontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), null, null, null, DEFAULT_STATE);

        BaseResponse expectedResponse = BaseResponse.failure("some validation failed");
        doThrow(new ValidationException("some validation failed")).when(frontLineWorkerContactCenterService).updateVerifiedFlw(expectedFrontLineWorkerWebRequest);

        postFLWRequestXml(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest(), "contact_center");

        ArgumentCaptor<FrontLineWorkerVerificationWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerVerificationWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerVerificationWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(expectedFrontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldValidateChannelParam() throws Exception {
        FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest = successfulFrontLineWorkerVerificationWebRequest(flwId, "1234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), "district", "block", "panchayat", "state");
        BaseResponse expectedResponse = BaseResponse.failure("invalid channel: comedy_central");

        postFLWRequestXml(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest(), "comedy_central");

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerVerificationWebRequest.class));
    }

    private void postFLWRequestXml(FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest, BaseResponse expectedResponse, ResultMatcher statusMatcher, String channel) throws Exception {
        MvcResult result = mockMvc(frontLineWorkerController)
                .perform(post("/flw").body(TestUtils.toXml(FrontLineWorkerVerificationWebRequest.class, frontLineWorkerWebRequest).getBytes()).param("channel", channel)
                .contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andExpect(statusMatcher)
                .andExpect(content().type("application/xml"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        BaseResponse actualResponse = TestUtils.fromXml(BaseResponse.class, responseString);
        assertEquals(expectedResponse, actualResponse);
    }

    private void postFlwRequestJson(FrontLineWorkerVerificationWebRequest frontLineWorkerWebRequest, BaseResponse expectedResponse, ResultMatcher statusMatcher) throws Exception {
        MvcResult result = mockMvc(frontLineWorkerController)
                .perform(post("/flw").body(TestUtils.toJson(frontLineWorkerWebRequest).getBytes()).param("channel", channel)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(statusMatcher)
                .andExpect(content().type("application/json"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        BaseResponse actualResponse = TestUtils.fromJson(BaseResponse.class, responseString);
        assertEquals(expectedResponse, actualResponse);
    }


    private FrontLineWorkerVerificationWebRequest failedFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String reason) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus).withReason(reason);
        builder.withFailedVerification(true);
        return builder.build();
    }

    public FrontLineWorkerVerificationWebRequest successfulFrontLineWorkerVerificationWebRequest(String flwId, String msisdn, String verificationStatus, String name, String designation, String district, String block, String panchayat, String state) {
        FrontLineWorkerVerificationWebRequestBuilder builder = new FrontLineWorkerVerificationWebRequestBuilder();
        builder.withDefaults().withFlwId(flwId).withMsisdn(msisdn).withVerificationStatus(verificationStatus);
        builder.withName(name).withDesignation(designation).withDistrict(district).withBlock(block).withPanchayat(panchayat)
        .withState(state)
        .withAlternateContactNumber("");
        return builder.build();
    }
}
