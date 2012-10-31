package org.motechproject.ananya.referencedata.contactCenter.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.motechproject.ananya.referencedata.flw.validators.Errors;

public class LocationWebRequestValidator {

    public static Errors validate(LocationRequest locationRequest) {
        Errors errors = new Errors();

        if (locationRequest == null) {
            errors.add("location is missing");
            return errors;
        }

        if (StringUtils.isEmpty(locationRequest.getDistrict()))
            errors.add("district field is blank");
        if (StringUtils.isEmpty(locationRequest.getBlock()))
            errors.add("block field is blank");
        if (StringUtils.isEmpty(locationRequest.getPanchayat()))
            errors.add("panchayat field is blank");
        return errors;
    }
}
