package org.motechproject.ananya.referencedata.web.controller;

import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.contactCenter.validator.WebRequestValidator;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.response.BaseResponse;
import org.motechproject.ananya.referencedata.flw.validators.CSVRequestValidationException;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.flw.validators.ValidationException;
import org.motechproject.ananya.referencedata.web.mapper.LocationResponseMapper;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.service.DefaultRequestValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.apache.commons.lang.WordUtils.capitalizeFully;

@Controller
public class LocationController extends BaseController {

    private LocationService locationService;
    private DefaultRequestValues defaultRequestValues;

    @Autowired
    public LocationController(LocationService locationService,
                              DefaultRequestValues defaultRequestValues) {
        this.locationService = locationService;
        this.defaultRequestValues = defaultRequestValues;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/alllocations", produces = "text/csv")
    public
    @ResponseBody
    LocationResponseList getLocationMaster(@RequestParam String channel, @RequestParam(required = false) String state) throws IOException {
        if(state == null) state = defaultRequestValues.getState();
        validateRequest(channel);
        return LocationResponseMapper.mapValidLocations(locationService.getAllValidLocations(capitalizeFully(state)));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/location")
    @ResponseBody
    public BaseResponse syncLocation(@RequestBody LocationRequest locationRequest) {
        defaultRequestValues.update(locationRequest);
        validateLocation(locationRequest);
        locationService.createAndFetch(locationRequest);
        return BaseResponse.success("New location has been synchronized successfully.");
    }

    private void validateRequest(String channel) {
        Errors errors = new Errors();
        new WebRequestValidator().validateChannel(channel, errors);
        if (errors.hasErrors()) {
            throw new CSVRequestValidationException(errors.allMessages());
        }
    }

    private void validateLocation(LocationRequest locationRequest) {
        Errors errors = new Errors();
        new WebRequestValidator().validateLocation(locationRequest, errors);
        if (errors.hasErrors()) {
            throw new ValidationException(errors.allMessages());
        }
    }
}
