package org.motechproject.ananya.referencedata.web.controller;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.csv.CsvImporter;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    private LocationService locationService;
    private CsvImporter csvImporter;

    @Autowired
    public HomeController(LocationService locationService, CsvImporter csvImporter) {
        this.locationService = locationService;
        this.csvImporter = csvImporter;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/admin", "/admin/home"})
    public ModelAndView home() {
        return new ModelAndView("admin/home");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/locationsToBeVerified/download", produces = "text/csv")
    @ResponseBody
    public LocationResponseList getLocationsToBeVerified() throws IOException {
        return LocationResponseMapper.mapLocationsToBeVerified(locationService.getLocationsToBeVerified());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/admin/location/upload")
    public ModelAndView uploadLocations(@ModelAttribute("csvUpload") CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse) throws IOException {
        byte[] errors = csvImporter.importLocation(csvUploadRequest.getFileData());
        if (errors.length > 0) {
            downloadErrorCsv(httpServletResponse, errors);
            return null;
        }
        return new ModelAndView("admin/home").addObject("successMessage", "Locations Uploaded Successfully.");
    }

    private void downloadErrorCsv(HttpServletResponse httpServletResponse, byte[] errors) throws IOException {
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=errors.csv");
        OutputStream outputStream = httpServletResponse.getOutputStream();
        FileCopyUtils.copy(errors, outputStream);
        outputStream.flush();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ModelAndView handleException(final Exception exception, HttpServletResponse response) throws IOException {
        logger.error(getExceptionString(exception));
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return new ModelAndView("admin/home").addObject("errorMessage","The system is down. Please try after sometime.");
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
