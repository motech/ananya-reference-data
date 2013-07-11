package org.motechproject.ananya.referencedata.csv.request;

import org.junit.Test;
import org.motechproject.importer.model.CSVDataImportProcessor;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FrontLineWorkerImportRequestTest {

    @Test
    public void shouldMapAllCSVColumns() {
        CSVDataImportProcessor csvDataImportProcessor = new CSVDataImportProcessor(FrontLineWorkerImportRequest.class);
        UUID uuid = UUID.randomUUID();
        Reader reader = new StringReader(
                "id,msisdn,alternate_contact_number,name,designation,verification_status,state,district,block,panchayat\n" +
                       uuid+",1234567892,1234567893,Kumari Manju,ANM,SUCCESS,Bihar,Patna,Barh,Sabnima");
        List<Object> content = csvDataImportProcessor.parse(reader);
        assertEquals(1, content.size());
        assertTrue(content.get(0) instanceof FrontLineWorkerImportRequest);
        FrontLineWorkerImportRequest flw = (FrontLineWorkerImportRequest) content.get(0);
        assertEquals("Bihar", flw.getLocation().getState());
        assertEquals("Patna", flw.getLocation().getDistrict());
        assertEquals("Barh", flw.getLocation().getBlock());
        assertEquals("Sabnima", flw.getLocation().getPanchayat());
        assertEquals("1234567892", flw.getMsisdn());
        assertEquals("1234567893", flw.getAlternateContactNumber());
        assertEquals("Kumari Manju", flw.getName());
        assertEquals("ANM", flw.getDesignation());
        assertEquals("SUCCESS", flw.getVerificationStatus());
        assertEquals(uuid.toString(), flw.getId());
    }

    @Test
    public void foo() {
        System.out.println(UUID.randomUUID().toString());
    }

}
