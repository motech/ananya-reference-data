package org.motechproject.ananya.referencedata.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.motechproject.ananya.referencedata.contactCenter.service.LocationService;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.validators.CSVRequestValidationException;
import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.response.LocationResponse;
import org.motechproject.ananya.referencedata.web.response.LocationResponseList;
import org.motechproject.ananya.referencedata.web.validator.WebRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class LocationController extends BaseController {

    private LocationService locationService;
    private WebRequestValidator webRequestValidator;

    @Autowired
    public LocationController(LocationService locationService, WebRequestValidator webRequestValidator) {
        this.locationService = locationService;
        this.webRequestValidator = webRequestValidator;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/alllocations", produces = "text/csv")
    public
    @ResponseBody
    LocationResponseList getLocationMaster(@RequestParam String channel) throws IOException {
        validateRequest(channel);
        return mapFrom(locationService.getAllValidLocations());
    }

    private void validateRequest(String channel) {
        Errors errors = webRequestValidator.validateChannel(channel);
        if (errors.hasErrors()) {
            throw new CSVRequestValidationException(errors.allMessages());
        }
    }

    private LocationResponseList mapFrom(List<Location> locationList) {
        List<LocationResponse> locationResponses = (List<LocationResponse>) CollectionUtils.collect(locationList, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location request = (Location) input;
                return new LocationResponse(request.getDistrict(), request.getBlock(), request.getPanchayat());
            }
        });
        return new LocationResponseList(locationResponses);
    }
}
