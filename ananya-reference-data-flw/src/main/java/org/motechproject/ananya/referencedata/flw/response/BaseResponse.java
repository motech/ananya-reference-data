package org.motechproject.ananya.referencedata.flw.response;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.StringWriter;

@XmlRootElement(name = "response")
public class BaseResponse {

    private static final String FAILED = "FAILED";
    private static final String SUCCESS = "SUCCESS";

    @JsonProperty
    @XmlElement
    protected String status;
    @JsonProperty
    @XmlElement
    protected String description;

    public BaseResponse(String status) {
        this.status = status;
    }

    private BaseResponse(String status, String description) {
        this.status = status;
        this.description = description;
    }

    BaseResponse() {
    }

    public static BaseResponse success(String description) {
        return new BaseResponse(SUCCESS, description);
    }

    public static BaseResponse failure(String description) {
        return new BaseResponse(FAILED, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResponse)) return false;

        BaseResponse that = (BaseResponse) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}