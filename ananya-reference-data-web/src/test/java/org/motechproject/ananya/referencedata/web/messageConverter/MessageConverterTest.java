package org.motechproject.ananya.referencedata.web.messageConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MvcResult;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MessageConverterTest {

    @Autowired
    private TestController testController;

    @Test
    public void shouldParseXMLRequestsAndReturnXMLResponse() throws Exception {
        MvcResult mvcResult = MVCTestUtils.mockMvc(testController)
                .perform(post("/test/")
                        .body(TestRequest.defaultXmlRequest.getBytes())
                        .contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().type("application/xml"))
                .andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testResponse><id>1</id><message>success</message><reason>foo</reason><beneficiaries><beneficiary><name>ben1</name></beneficiary><beneficiary><name>ben2</name></beneficiary></beneficiaries></testResponse>", responseContent);

    }

    @Test
    public void shouldParseJsonRequestsAndReturnJSONResponse() throws Exception {
        MvcResult mvcResult = MVCTestUtils.mockMvc(testController)
                .perform(post("/test/")
                        .body(TestRequest.defaultJsonRequest.getBytes())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json"))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("{\"id\":1,\"message\":\"success\",\"reason\":\"foo\",\"beneficiaryList\":[{\"name\":\"ben1\"},{\"name\":\"ben2\"}]}", contentAsString);

    }

    @Test
    public void shouldReturnDefaultResponseAsJSON() throws Exception {
        MVCTestUtils.mockMvc(testController)
                .perform(post("/test/")
                        .body(TestRequest.defaultXmlRequest.getBytes())
                        .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().type("application/json"))
                .andReturn();
    }
}
