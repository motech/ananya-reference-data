package org.motechproject.ananya.referencedata.web.functional.api;

import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.Header;
import com.eclipsesource.restfuse.annotation.HttpTest;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.RunWith;
import org.motechproject.ananya.referencedata.web.FunctionalTest;

import java.io.IOException;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static org.junit.Assert.assertEquals;

@RunWith(HttpJUnitRunner.class)
public class FrontLineWorkerApiTest extends FunctionalTest {
    public static final String FLW_DETAILS_WITHOUT_STATE = "" +
            "<flw>" +
            "    <id>e883939c-7872-434d-bc51-4087fc2e60d8</id>" +
            "    <msisdn>7250098574</msisdn>" +
            "    <alternateContactNumber></alternateContactNumber>" +
            "    <verificationStatus>SUCCESS</verificationStatus>" +
            "    <name>Sunita Devi6</name>" +
            "    <designation>AWW</designation>" +
            "    <location>" +
            "        <district>D8</district>" +
            "        <block>B1</block>" +
            "        <panchayat>P1</panchayat>" +
            "    </location>" +
            "</flw>";

    public static final String FLW_DETAILS_WITH_STATE = "" +
            "<flw>" +
            "    <id>e983939c-7872-434d-bc51-4087fc2e60d8</id>" +
            "    <msisdn>1234567890</msisdn>" +
            "    <alternateContactNumber>1234567891</alternateContactNumber>" +
            "    <verificationStatus>SUCCESS</verificationStatus>" +
            "    <name>Sunita Devi</name>" +
            "    <designation>AWW</designation>" +
            "    <location>" +
            "        <district>D8</district>" +
            "        <block>B1</block>" +
            "        <panchayat>P1</panchayat>" +
            "        <state>Orissa</state>" +
            "    </location>" +
            "</flw>";

    @Context
    private Response response;

    @HttpTest(method = Method.POST, path = "{appPath}/flw/?channel={channel}",
            headers = {@Header(name = "Content-Type", value = "application/xml"),
                    @Header(name = "{APIKeyName}", value = "{APIKeyValue}")},
            content = FLW_DETAILS_WITHOUT_STATE)
    public void shouldCreateVerifiedFLWhenStateIsMissing() throws IOException {
        assertOk(response);
        assertEquals("SUCCESS", status());
    }

    @HttpTest(method = Method.POST, path = "{appPath}/flw/?channel={channel}",
            headers = {@Header(name = "Content-Type", value = "application/xml"),
                    @Header(name = "{APIKeyName}", value = "{APIKeyValue}")},
            content = FLW_DETAILS_WITH_STATE)
    public void shouldCreateVerifiedFLWithState() throws IOException {
        assertOk(response);
        assertEquals("SUCCESS", status());
    }

    private String status() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readValue(response.getBody(), JsonNode.class);
        return node.get("status").asText();
    }

}
