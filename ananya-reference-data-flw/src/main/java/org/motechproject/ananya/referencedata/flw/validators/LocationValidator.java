package org.motechproject.ananya.referencedata.flw.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.repository.AllLocations;
import org.motechproject.ananya.referencedata.flw.response.FLWValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationValidator {

    private AllLocations allLocations;

    @Autowired
    public LocationValidator(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public FLWValidationResponse validate(Location location) {
        FLWValidationResponse responseFLW = new FLWValidationResponse();
        if (StringUtils.isEmpty(location.getDistrict()) || StringUtils.isEmpty(location.getBlock()) || StringUtils.isEmpty(location.getPanchayat()))
            responseFLW.forBlankFieldsInLocation();
        Location alreadyPresentLocation = allLocations.getFor(location.getDistrict(), location.getBlock(), location.getPanchayat());
        if (alreadyPresentLocation != null)
            responseFLW.forDuplicateLocation();
        return responseFLW;
    }
}
