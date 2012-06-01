package org.motechproject.ananya.referencedata.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.domain.Location;
import org.motechproject.ananya.referencedata.domain.LocationList;
import org.motechproject.ananya.referencedata.response.ValidationResponse;

public class LocationValidator {

    private final LocationList locationList;

    public LocationValidator(LocationList locationList) {
        this.locationList = locationList;
    }

    public ValidationResponse validate(Location location) {
        ValidationResponse response = new ValidationResponse();
        if(StringUtils.isEmpty(location.getDistrict()) || StringUtils.isEmpty(location.getBlock()) || StringUtils.isEmpty(location.getPanchayat()))
            return response.forBlankFieldsInLocation();
        if(locationList.isAlreadyPresent(location))
            return response.forDuplicateLocation();
        return response.forSuccessfulCreationOfLocation();
    }
}
