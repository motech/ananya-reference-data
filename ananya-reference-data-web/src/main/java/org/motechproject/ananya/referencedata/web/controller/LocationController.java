package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.contactCenter.validator.WebRequestValidator;
import org.motechproject.ananya.referencedata.flw.validators.CSVRequestValidationException;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class LocationController extends BaseController {

    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/alllocations", produces = "text/csv")
    public
    @ResponseBody
    LocationResponseList getLocationMaster(@RequestParam String channel) throws IOException {
        validateRequest(channel);
        return LocationResponseMapper.mapWithoutStatus(locationService.getAllValidLocations());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/locationsToBeVerified", produces = "text/csv")
    public
    @ResponseBody
    LocationResponseList getLocationsToBeVerified(@RequestParam String channel) throws IOException {
        validateRequest(channel);
        return LocationResponseMapper.mapWithStatus(locationService.getLocationsToBeVerified());
    }

    private void validateRequest(String channel) {
        Errors errors = new Errors();
        new WebRequestValidator().validateChannel(channel, errors);
        if (errors.hasErrors()) {
            throw new CSVRequestValidationException(errors.allMessages());
        }
    }


}
