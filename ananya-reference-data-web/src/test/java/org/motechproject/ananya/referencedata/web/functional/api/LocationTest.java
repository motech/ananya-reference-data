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
import org.motechproject.ananya.referencedata.web.functional.framework.FunctionalTestCase;

import java.io.IOException;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(HttpJUnitRunner.class)
public class LocationTest extends FunctionalTestCase {
    public static final String LOCATION_WITHOUT_STATE = "" +
            "{ " +
            "\"district\" :\"Koraput\", " +
            "\"block\" : \"Jeypore\", " +
            "\"panchayat\" : \"Masigan\"  " +
            "}";

    public static final String LOCATION_WITH_STATE = "" +
            "{ " +
            "\"district\" :\"Koraput\", " +
            "\"block\" : \"Jeypore\", " +
            "\"panchayat\" : \"Masigan\",  " +
            "\"state\" : \"Orissa\"  " +
            "}";
    public static final String LOCATION_HEADERS = "state,district,block,panchayat";

    @Context
    private Response response;

    @HttpTest(method = Method.POST, path = "{appPath}/location",
            headers = {@Header(name = "Content-Type", value = "application/json"),
                    @Header(name = "{APIKeyName}", value = "{APIKeyValue}")},
            content = LOCATION_WITHOUT_STATE)
    public void shouldCreateNewLocationWithoutState() throws IOException {
        assertOk(response);
        assertEquals("SUCCESS", status());
    }

    @HttpTest(method = Method.POST, path = "{appPath}/location",
            headers = {@Header(name = "Content-Type", value = "application/json"),
                    @Header(name = "{APIKeyName}", value = "{APIKeyValue}")},
            content = LOCATION_WITH_STATE)
    public void shouldCreateLocationWithState() throws IOException {
        assertOk(response);
        assertEquals("SUCCESS", status());
    }

    @HttpTest(method = Method.GET, path = "{appPath}/alllocations?channel={channel}",
            headers = {
                    @Header(name = "{APIKeyName}", value = "{APIKeyValue}")})
    public void shouldGetAllLocations() throws IOException {
        assertOk(response);
        assertThat(response.getBody(), containsString(LOCATION_HEADERS));
    }

    private String status() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readValue(response.getBody(), JsonNode.class);
        return node.get("status").asText();
    }

}
