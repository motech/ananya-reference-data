package org.motechproject.ananya.referencedata.web.messageConverter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.referencedata.web.Beneficiary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "flw")
public class TestRequest {
    public static String defaultXmlRequest ="<flw>\n" +
            "    <id>flw-id</id>\n" +
            "    <verification-status>INVALID</verification-status>\n" +
            "    <reason>Invalid User</reason>\n" +
            "    <beneficiaries>\n" +
            "       <beneficiary><name>beneficiary1</name></beneficiary>\n" +
            "       <beneficiary><name>beneficiary2</name></beneficiary>\n" +
            "    </beneficiaries>\n" +
            "</flw>";

    public static String defaultJsonRequest = "{\"id\":1,\"verification-status\": \"SUCCESS\", \"reason\":\"foo\", \"beneficiaries\":[{\"name\":\"beneficiary1\"},{\"name\":\"beneficiary2\"}]}";

    @JsonProperty
    @XmlElement
    private String id;

    @XmlElement(name = "verification-status")
    @JsonProperty(value = "verification-status")
    private String verificationStatus;

    @XmlElement(name = "reason")
    @JsonProperty
    private String reason;

    @XmlElement(name = "beneficiary")
    @XmlElementWrapper(name = "beneficiaries")
    @JsonProperty
    private List<Beneficiary> beneficiaries;

}
