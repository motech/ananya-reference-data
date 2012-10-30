package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerWebRequest;
import org.motechproject.ananya.referencedata.contactCenter.service.FrontLineWorkerContactCenterService;
import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.web.utils.TestUtils;
import org.motechproject.ananya.referencedata.web.validator.WebRequestValidator;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class FrontLineWorkerControllerTest {

    @Mock
    private FrontLineWorkerContactCenterService frontLineWorkerContactCenterService;
    @Mock
    private WebRequestValidator webRequestValidator;

    private FrontLineWorkerController frontLineWorkerController;
    private String flwId = UUID.randomUUID().toString();
    private String channel = "contact_center";

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerController = new FrontLineWorkerController(frontLineWorkerContactCenterService);
    }

    @Test
    public void shouldUpdateTheStatusForAValidFLWRequestXml() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.INVALID.name(), "Invalid User");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.success(), status().isOk(), channel);

        ArgumentCaptor<FrontLineWorkerWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidFLWRequestXml() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", null, "Invalid User");

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.failure("verificationStatus field has invalid/blank value"), status().isBadRequest(), "contact_center");

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerWebRequest.class));
    }

    @Test
    public void shouldUpdateTheStatusForAValidFLWRequestJson() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.INVALID.name(), "Invalid User");

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.success(), status().isOk());

        ArgumentCaptor<FrontLineWorkerWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidFLWRequestJson() throws Exception {
        String reason = "Invalid User";
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", null, reason);

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.failure("verificationStatus field has invalid/blank value"), status().isBadRequest());

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerWebRequest.class));
    }

    @Test
    public void shouldUpdateTheStatusForAValidSuccessfulFLWRequestJson() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"));

        postFlwRequestJson(frontLineWorkerWebRequest, BaseResponse.success(), status().isOk());

        ArgumentCaptor<FrontLineWorkerWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidSuccessfulFLWRequestJson() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), new LocationRequest());
        BaseResponse expectedResponse = BaseResponse.failure("name field has invalid/blank value,district field is blank,block field is blank,panchayat field is blank");

        postFlwRequestJson(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest());

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerWebRequest.class));
    }

    @Test
    public void shouldUpdateTheStatusForAValidSuccessfulFLWRequestXml() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"));

        postFLWRequestXml(frontLineWorkerWebRequest, BaseResponse.success(), status().isOk(), "contact_center");

        ArgumentCaptor<FrontLineWorkerWebRequest> captor = ArgumentCaptor.forClass(FrontLineWorkerWebRequest.class);
        verify(frontLineWorkerContactCenterService).updateVerifiedFlw(captor.capture());
        FrontLineWorkerWebRequest deserializedFrontLineWorkerWebRequest = captor.getValue();
        assertEquals(frontLineWorkerWebRequest, deserializedFrontLineWorkerWebRequest);
    }

    @Test
    public void shouldReturnValidationErrorForAnInvalidSuccessfulFLWRequestXml() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.SUCCESS.name(), null, Designation.ANM.name(), new LocationRequest());
        BaseResponse expectedResponse = BaseResponse.failure("name field has invalid/blank value,district field is blank,block field is blank,panchayat field is blank");

        postFLWRequestXml(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest(), "contact_center");

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerWebRequest.class));
    }

    @Test
    public void shouldValidateChannelParam() throws Exception {
        FrontLineWorkerWebRequest frontLineWorkerWebRequest = new FrontLineWorkerWebRequest(flwId, "911234567890", VerificationStatus.SUCCESS.name(), "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"));
        BaseResponse expectedResponse = BaseResponse.failure("Invalid channel: comedy_central");

        postFLWRequestXml(frontLineWorkerWebRequest, expectedResponse, status().isBadRequest(), "comedy_central");

        verify(frontLineWorkerContactCenterService, never()).updateVerifiedFlw(any(FrontLineWorkerWebRequest.class));
    }

    private void postFLWRequestXml(FrontLineWorkerWebRequest frontLineWorkerWebRequest, BaseResponse expectedResponse, ResultMatcher statusMatcher, String channel) throws Exception {
        MvcResult result = mockMvc(frontLineWorkerController)
                .perform(post("/flw").body(TestUtils.toXml(FrontLineWorkerWebRequest.class, frontLineWorkerWebRequest).getBytes()).param("channel", channel)
                        .contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andExpect(statusMatcher)
                .andExpect(content().type("application/xml"))
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        BaseResponse actualResponse = TestUtils.fromXml(BaseResponse.class, responseString);
        assertEquals(expectedResponse, actualResponse);
    }

    private void postFlwRequestJson(FrontLineWorkerWebRequest frontLineWorkerWebRequest, BaseResponse expectedResponse, ResultMatcher statusMatcher) throws Exception {
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
}
