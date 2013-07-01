package org.motechproject.ananya.referencedata.csv.request;

import org.junit.Test;
import org.motechproject.importer.model.CSVDataImportProcessor;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class FrontLineWorkerImportRequestTest {

    @Test
    public void shouldMapAllCSVColumns() {
        CSVDataImportProcessor csvDataImportProcessor = new CSVDataImportProcessor(FrontLineWorkerImportRequest.class);
        Reader reader = new StringReader(
                "msisdn,name,designation,state,district,block,panchayat\n" +
                        "1234567892,Kumari Manju,ANM,Bihar,Patna,Barh,Sabnima");
        List<Object> content = csvDataImportProcessor.parse(reader);
        assertEquals(1, content.size());
        assertTrue(content.get(0) instanceof FrontLineWorkerImportRequest);
        FrontLineWorkerImportRequest flw = (FrontLineWorkerImportRequest) content.get(0);
        assertEquals("Bihar", flw.getLocation().getState());
        assertEquals("Patna", flw.getLocation().getDistrict());
        assertEquals("Barh", flw.getLocation().getBlock());
        assertEquals("Sabnima", flw.getLocation().getPanchayat());
        assertEquals("1234567892", flw.getMsisdn());
        assertEquals("Kumari Manju", flw.getName());
        assertEquals("ANM", flw.getDesignation());
    }

}
