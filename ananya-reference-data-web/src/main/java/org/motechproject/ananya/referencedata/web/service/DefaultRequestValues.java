package org.motechproject.ananya.referencedata.web.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultRequestValues {

    private String defaultState;

    @Autowired
    public DefaultRequestValues(@Value("#{referencedataProperties['location.default.state']}") String defaultState) {
        this.defaultState = defaultState;
    }

    public void update(LocationRequest locationRequest) {
        if(StringUtils.isEmpty(locationRequest.getState())){
            locationRequest.setState(defaultState);
        }
    }

    public void update(FrontLineWorkerVerificationWebRequest request) {
        LocationRequest location = request.getLocation();
        if(location != null)
            update(location);
    }

    public String getState() {
        return defaultState;
    }
}
