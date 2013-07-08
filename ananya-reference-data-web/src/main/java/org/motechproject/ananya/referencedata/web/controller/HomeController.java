package org.motechproject.ananya.referencedata.web.controller;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.csv.ImportType;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.importer.model.AllCSVDataImportProcessor;
import org.motechproject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HomeController {
    public static final String NEW_LINE = "\r\n|\r|\n";
    public static final Pattern NEW_LINE_PATTERN = Pattern.compile(NEW_LINE);
    public static final int MAXIMUM_NUMBER_OF_RECORDS = 5000;
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    private LocationService locationService;
    private AllCSVDataImportProcessor allCSVDataImportProcessor;

    @Autowired
    public HomeController(LocationService locationService, AllCSVDataImportProcessor allCSVDataImportProcessor) {
        this.locationService = locationService;
        this.allCSVDataImportProcessor = allCSVDataImportProcessor;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/admin", "/admin/home"})
    public ModelAndView home() {
        return new ModelAndView("admin/home");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/locationsToBeVerified/download", produces = "text/csv")
    @ResponseBody
    public LocationResponseList getLocationsToBeVerified() throws IOException {
        try {
            return LocationResponseMapper.mapLocationsToBeVerified(locationService.getLocationsToBeVerified());
        } catch (Exception e) {
            logger.error(getExceptionString(e));
            throw new RuntimeException("The system is down. Please try after some time.");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/flw/upload")
    public ModelAndView uploadFrontLineWorkers(@ModelAttribute("csvUpload") CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse) throws Exception {
        return uploadFile(csvUploadRequest, httpServletResponse, ImportType.FrontLineWorker);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/location/upload")
    public ModelAndView uploadLocations(@ModelAttribute("csvUpload") CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse) throws Exception {
        return uploadFile(csvUploadRequest, httpServletResponse, ImportType.Location);
    }

    private ModelAndView uploadFile(CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse, ImportType entity) throws Exception {
        String csvContent = csvUploadRequest.getStringContent();
        if (exceedsMaximumNumberOfRecordsLimit(csvContent)) {
            return new ModelAndView("admin/home").addObject("errorMessage", entity.errorMessage());
        }
        String response = allCSVDataImportProcessor.get(entity.name()).processContent(csvContent);
        if (response != null) {
            downloadErrorCsv(httpServletResponse, response, entity.responseFilePrefix());
            return null;
        }
        return new ModelAndView("admin/home").addObject("successMessage", entity.successMessage());
    }

    private boolean exceedsMaximumNumberOfRecordsLimit(String csvContent) {
        int count = 0;
        Matcher matcher = NEW_LINE_PATTERN.matcher(csvContent);
        while (matcher.find())
            count++;
        return count > MAXIMUM_NUMBER_OF_RECORDS;
    }

    private void downloadErrorCsv(HttpServletResponse httpServletResponse, String errorCsv, String responseFilePrefix) throws IOException {
        String fileName = responseFilePrefix + DateTime.now().toString("yyyy-MM-dd'T'HH:mm") + ".csv";
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(errorCsv.getBytes());
        outputStream.flush();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ModelAndView handleException(final Exception exception, HttpServletResponse response) throws IOException {
        logger.error(getExceptionString(exception));
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        String errorMessage = "An error has occurred";
        if (!StringUtil.isNullOrEmpty(exception.getMessage()))
            errorMessage += " : " + exception.getMessage();
        return new ModelAndView("admin/home").addObject("errorMessage", errorMessage);
    }

    private String getExceptionString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ExceptionUtils.getMessage(ex));
        sb.append(ExceptionUtils.getStackTrace(ex));
        sb.append(ExceptionUtils.getRootCauseMessage(ex));
        sb.append(ExceptionUtils.getRootCauseStackTrace(ex));
        return sb.toString();
    }
}

