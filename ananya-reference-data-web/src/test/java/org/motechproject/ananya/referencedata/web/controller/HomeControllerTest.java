package org.motechproject.ananya.referencedata.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.csv.ImportType;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.importer.model.AllCSVDataImportProcessor;
import org.motechproject.importer.model.CSVDataImportProcessor;
import org.springframework.test.web.server.MvcResult;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.motechproject.ananya.referencedata.web.utils.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    @Mock
    private LocationService locationService;
    @Mock
    private AllCSVDataImportProcessor allCSVDataImportProcessor;

    private HomeController homeController;
    private CommonsMultipartFile fileData;
    private HttpServletResponse response;
    private ServletOutputStream outputStream;
    private CSVDataImportProcessor csvDataImportProcessor;
    private Properties properties;

    @Before
    public void setup() {
        properties = new Properties();
        properties.setProperty("flw.csv.max.records", "50");
        properties.setProperty("msisdn.csv.max.records", "50");
        homeController = new HomeController(locationService, allCSVDataImportProcessor, properties);
    }

    @Test
    public void shouldReturnLocationsToBeVerifiedAsCSV() throws Exception {
        Location location = new Location("S", "D", "B", "P", LocationStatus.NOT_VERIFIED, null);
        when(locationService.getLocationsToBeVerified()).thenReturn(Arrays.asList(location));

        MvcResult mvcResult = mockMvc(homeController).perform(get("/admin/locationsToBeVerified/download"))
                .andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertTrue(contentAsString.contains("state,district,block,panchayat,status,newState,newDistrict,newBlock,newPanchayat"));
        assertTrue(contentAsString.contains("S,D,B,P,NOT VERIFIED,,"));
    }

    @Test
    public void shouldThrowExceptionOnError() throws Exception {
        when(locationService.getLocationsToBeVerified()).thenThrow(new RuntimeException("aragorn"));

        MvcResult mvcResult = mockMvc(homeController).perform(get("/admin/locationsToBeVerified/download"))
                .andExpect(status().is(500)).andReturn();
        assertEquals("An error has occurred : The system is down. Please try after some time.", mvcResult.getModelAndView().getModelMap().get("errorMessage"));
    }

    @Test
    public void shouldUploadLocationsFile() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.Location.name());
        String errorCsv = "response";
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), false)).thenReturn(errorCsv);

        homeController.uploadLocations(csvFileRequest, response);

        verify(outputStream).write(errorCsv.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=location_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
        verify(outputStream).flush();
    }

    @Test
    public void shouldRespondWithSuccessMessageOnLocationsUpload() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.Location.name());
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), false)).thenReturn(null);
        ModelAndView modelAndView = homeController.uploadLocations(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("Locations Uploaded Successfully.", modelAndView.getModel().get("successMessage"));
    }

    @Test
    public void shouldUploadFLWFile() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.FrontLineWorker.name());
        String errorCsv = "response";
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), false)).thenReturn(errorCsv);

        homeController.uploadFrontLineWorkers(csvFileRequest, response);

        verify(outputStream).write(errorCsv.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=flw_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
        verify(outputStream).flush();
    }

    @Test
    public void shouldRespondWithSuccessMessageOnFLWUpload() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.FrontLineWorker.name());
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), false)).thenReturn(null);
        ModelAndView modelAndView = homeController.uploadFrontLineWorkers(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("FLWs Uploaded Successfully.", modelAndView.getModel().get("successMessage"));
    }

    @Test
    public void shouldRespondWithErrorMessageOnUploadOfMoreThan51FLWRecords() throws Exception {
        String csvWith51Records = createCSVRecordsWith(51);
        CsvUploadRequest csvFileRequest = mock(CsvUploadRequest.class);
        when(csvFileRequest.getStringContent()).thenReturn(csvWith51Records);
        ModelAndView modelAndView = homeController.uploadFrontLineWorkers(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("FLW file can have a maximum of 50 records.", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    public void shouldUploadMSISDNFileSuccessfullyAndConsiderValidRecords() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.Msisdn.name());
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), true)).thenReturn(null);

        ModelAndView modelAndView = homeController.uploadMSISDNs(csvFileRequest, this.response);

        verify(csvDataImportProcessor).processContent(csvFileRequest.getStringContent(), true);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("MSISDNs have been updated successfully.", modelAndView.getModel().get("successMessage"));
    }

    @Test
    public void shouldRespondWithErrorsCSVIfAnyErrorDuringMSISDNImport() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.Msisdn.name());
        String errorCSV = "errorCSV";
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent(), true)).thenReturn(errorCSV);

        homeController.uploadMSISDNs(csvFileRequest, response);

        verify(outputStream).write(errorCSV.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=msisdn_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
        verify(outputStream).flush();
    }

    @Test
    public void shouldRespondWithErrorMessageOnUploadOfMoreThanAllowedNumberOfMsisdnRecords() throws Exception {
        String csvWith51Records = createCSVRecordsWith(51);
        CsvUploadRequest csvFileRequest = mock(CsvUploadRequest.class);
        when(csvFileRequest.getStringContent()).thenReturn(csvWith51Records);

        ModelAndView modelAndView = homeController.uploadMSISDNs(csvFileRequest, response);

        verifyZeroInteractions(allCSVDataImportProcessor);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("MSISDN CSV file can have a maximum of 50 records.", modelAndView.getModel().get("errorMessage"));
    }

    static String createCSVRecordsWith(int numberOfRows) {
        String csv = "header1,header2\r";
        for (int rowCount = 1; rowCount <= numberOfRows; rowCount++)
            if (rowCount == numberOfRows) {
                csv += "recordA,recordB";
            } else if (rowCount % 2 == 0) {
                csv += "recordA,recordB\r\n";
            } else {
                csv += "recordA,recordB\n";
            }
        return csv;
    }

    private CsvUploadRequest mockCSVFileRequest(String entity) throws IOException {
        fileData = mock(CommonsMultipartFile.class);
        response = mock(HttpServletResponse.class);
        outputStream = mock(ServletOutputStream.class);
        csvDataImportProcessor = mock(CSVDataImportProcessor.class);
        when(response.getOutputStream()).thenReturn(outputStream);
        when(fileData.getBytes()).thenReturn(new byte[1]);
        when(allCSVDataImportProcessor.get(entity)).thenReturn(csvDataImportProcessor);
        return new CsvUploadRequest(fileData);
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionOnLocationUploadError() throws Exception {
        when(allCSVDataImportProcessor.get(ImportType.Location.name())).thenThrow(new Exception());

        MvcResult mvcResult = mockMvc(homeController).perform(post("/admin/location/upload").body(new byte[1]))
                .andExpect(status().is(500)).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("An error has occurred"));
    }

    @Test(expected = Exception.class)
    public void shouldThrowExceptionOnFLWUploadError() throws Exception {
        when(allCSVDataImportProcessor.get(ImportType.Location.name())).thenThrow(new Exception());

        MvcResult mvcResult = mockMvc(homeController).perform(post("/admin/flw/upload").body(new byte[1]))
                .andExpect(status().is(500)).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("An error has occurred"));
    }
}
