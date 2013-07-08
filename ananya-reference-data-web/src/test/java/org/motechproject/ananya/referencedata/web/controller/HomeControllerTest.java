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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
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

    @Before
    public void setup() {
        homeController = new HomeController(locationService, allCSVDataImportProcessor);
    }

    @Test
    public void shouldReturnLocationsToBeVerifiedAsCSV() throws Exception {
        Location location = new Location("D", "B", "P", "S", LocationStatus.NOT_VERIFIED, null);
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
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent())).thenReturn(errorCsv);

        homeController.uploadLocations(csvFileRequest, response);

        verify(outputStream).write(errorCsv.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=location_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
        verify(outputStream).flush();
    }

    @Test
    public void shouldRespondWithSuccessMessageOnLocationsUpload() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.Location.name());
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent())).thenReturn(null);
        ModelAndView modelAndView = homeController.uploadLocations(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("Locations Uploaded Successfully.", modelAndView.getModel().get("successMessage"));
    }

    @Test
    public void shouldUploadFLWFile() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.FrontLineWorker.name());
        String errorCsv = "response";
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent())).thenReturn(errorCsv);

        homeController.uploadFrontLineWorkers(csvFileRequest, response);

        verify(outputStream).write(errorCsv.getBytes());
        verify(response).setHeader(eq("Content-Disposition"), matches(
                "attachment; filename=flw_upload_failures\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.csv"));
        verify(outputStream).flush();
    }

    @Test
    public void shouldRespondWithSuccessMessageOnFLWUpload() throws Exception {
        CsvUploadRequest csvFileRequest = mockCSVFileRequest(ImportType.FrontLineWorker.name());
        when(csvDataImportProcessor.processContent(csvFileRequest.getStringContent())).thenReturn(null);
        ModelAndView modelAndView = homeController.uploadFrontLineWorkers(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("FLWs Uploaded Successfully.", modelAndView.getModel().get("successMessage"));
    }

    @Test
    public void shouldRespondWithErrorMessageOnUploadOfMoreThan5000FLWRecords() throws Exception {
        String csvWith5001Records = createCSVWith5001Records();
        CsvUploadRequest csvFileRequest = mock(CsvUploadRequest.class);
        when(csvFileRequest.getStringContent()).thenReturn(csvWith5001Records);
        ModelAndView modelAndView = homeController.uploadFrontLineWorkers(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("FLW file can have a maximum of 5000 records.", modelAndView.getModel().get("errorMessage"));
    }

    @Test
    public void shouldRespondWithErrorMessageOnUploadOfMoreThan5000LocationRecords() throws Exception {
        String csvWith5001Records = createCSVWith5001Records();
        CsvUploadRequest csvFileRequest = mock(CsvUploadRequest.class);
        when(csvFileRequest.getStringContent()).thenReturn(csvWith5001Records);
        ModelAndView modelAndView = homeController.uploadLocations(csvFileRequest, response);
        assertEquals("admin/home", modelAndView.getViewName());
        assertEquals("Location file can have a maximum of 5000 records.", modelAndView.getModel().get("errorMessage"));
    }

    private String createCSVWith5001Records() {
        String csv = "header1,header2\n";
        for(int i =0;i<5001;i++)
            csv += "recordA,recordB\n";
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
