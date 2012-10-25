package org.motechproject.ananya.referencedata.flw.response;

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

    public static BaseResponse success() {
        return new BaseResponse(SUCCESS);
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

//    public String toXml() throws JAXBException {
//        JAXBContext context = JAXBContext.newInstance(BaseResponse.class);
//        Marshaller m = context.createMarshaller();
//        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
//        StringWriter writer = new StringWriter();
//        m.marshal(this, writer);
//        return writer.toString();
//    }
}