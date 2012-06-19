package org.motechproject.ananya.referencedata.flw.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationList;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;

public class LocationValidator {

    private final LocationList locationList;

    public LocationValidator(LocationList locationList) {
        this.locationList = locationList;
    }

    public FLWValidationResponse validate(Location location) {
        FLWValidationResponse responseFLW = new FLWValidationResponse();
        if(StringUtils.isEmpty(location.getDistrict()) || StringUtils.isEmpty(location.getBlock()) || StringUtils.isEmpty(location.getPanchayat()))
            responseFLW.forBlankFieldsInLocation();
        if(locationList.isAlreadyPresent(location))
            responseFLW.forDuplicateLocation();
        return responseFLW;
    }
}
