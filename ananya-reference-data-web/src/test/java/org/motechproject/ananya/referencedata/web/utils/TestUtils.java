package org.motechproject.ananya.referencedata.web.utils;

import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TestUtils {

    public static String toXml(Class className, Object objectToSerialize) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(className);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();
        m.marshal(objectToSerialize, stringWriter);
        return stringWriter.toString();
    }

    public static <T> T fromXml(Class className, String xmlString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(className);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(new StringReader(xmlString));
    }

    public static String toJson(Object objectToSerialize) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, objectToSerialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static <T> T fromJson(Class<T> subscriberResponseClass, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, subscriberResponseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }
}
