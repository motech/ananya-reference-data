package org.motechproject.ananya.referencedata.web.messageConverter;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class TestResponse {
    @XmlElement
    @JsonProperty
    private int id;

    @XmlElement
    @JsonProperty
    private String message;

    @XmlElement
    @JsonProperty
    private String reason;

    @XmlElement(name="beneficiary")
    @XmlElementWrapper(name = "beneficiaries")
    @JsonProperty
    private List<Beneficiary> beneficiaryList;

    public TestResponse() {
    }

    public TestResponse(int id, String message, String reason, List<Beneficiary> beneficiaryList) {
        this.id = id;
        this.message = message;
        this.reason = reason;
        this.beneficiaryList = beneficiaryList;
    }
}
