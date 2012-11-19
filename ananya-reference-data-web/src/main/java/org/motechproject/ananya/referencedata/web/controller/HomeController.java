package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.csv.CsvImporter;
import org.motechproject.ananya.referencedata.web.domain.CsvUploadRequest;
import org.motechproject.ananya.referencedata.web.exception.PopUpException;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class HomeController extends BaseController {

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

    @RequestMapping(method = RequestMethod.GET, value = "/admin/location/download", produces = "text/csv")
    @ResponseBody
    public LocationResponseList locationResponseList() throws IOException {
        try {
            return LocationResponseMapper.mapWithStatus(locationService.getLocationsToBeVerified());
        } catch (Exception e) {
            throw new PopUpException(e.getMessage());
        }
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
}
