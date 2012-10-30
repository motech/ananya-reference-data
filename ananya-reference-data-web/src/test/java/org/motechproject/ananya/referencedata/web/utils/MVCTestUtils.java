package org.motechproject.ananya.referencedata.web.utils;

import org.motechproject.web.message.converters.CSVHttpMessageConverter;
import org.motechproject.web.message.converters.CustomJaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.test.web.server.setup.StandaloneMockMvcBuilder;

public class MVCTestUtils {

    public static MockMvc mockMvc(Object controller) {
        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(controller);
        mockMvcBuilder.setMessageConverters(new MappingJacksonHttpMessageConverter(), new CustomJaxb2RootElementHttpMessageConverter(), new CSVHttpMessageConverter());
        return mockMvcBuilder.build();
    }
}
