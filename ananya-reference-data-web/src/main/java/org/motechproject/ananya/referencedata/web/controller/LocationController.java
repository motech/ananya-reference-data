package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.contactCenter.validator.WebRequestValidator;
import org.motechproject.ananya.referencedata.csv.CsvImporter;
import org.motechproject.ananya.referencedata.flw.validators.CSVRequestValidationException;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class LocationController extends BaseController {

    private LocationService locationService;
    private CsvImporter csvImporter;

    @Autowired
    public LocationController(LocationService locationService, CsvImporter csvImporter) {
        this.locationService = locationService;
        this.csvImporter = csvImporter;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/alllocations", produces = "text/csv")
    public
    @ResponseBody
    LocationResponseList getLocationMaster(@RequestParam String channel) throws IOException {
        validateRequest(channel);
        return LocationResponseMapper.mapWithoutStatus(locationService.getAllValidLocations());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/locationsToBeVerified", produces = "text/csv")
    @ResponseBody
    public LocationResponseList getLocationsToBeVerified(@RequestParam String channel) throws IOException {
        validateRequest(channel);
        return LocationResponseMapper.mapWithStatus(locationService.getLocationsToBeVerified());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/locationsUpload")
    public void uploadLocations(@ModelAttribute("csvUpload") CsvUploadRequest csvUploadRequest, HttpServletResponse httpServletResponse) throws IOException {
        byte[] errors = csvImporter.importLocation(csvUploadRequest.getFileData());
        downloadErrorCsv(httpServletResponse, errors);
    }

    private void downloadErrorCsv(HttpServletResponse httpServletResponse, byte[] errors) throws IOException {
        httpServletResponse.setHeader("Content-Disposition",
                "attachment; filename=errors.csv");
        OutputStream outputStream = httpServletResponse.getOutputStream();
        FileCopyUtils.copy(errors, outputStream);
        outputStream.flush();
    }

    private void validateRequest(String channel) {
        Errors errors = new Errors();
        new WebRequestValidator().validateChannel(channel, errors);
        if (errors.hasErrors()) {
            throw new CSVRequestValidationException(errors.allMessages());
        }
    }
}
