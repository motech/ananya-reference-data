package org.motechproject.ananya.referencedata.web.validator;

import org.motechproject.ananya.referencedata.flw.validators.Errors;
import org.motechproject.ananya.referencedata.web.domain.Channel;
import org.springframework.stereotype.Component;

@Component
public class WebRequestValidator {

    public Errors validateChannel(String channel) {
        Errors errors = new Errors();
        if (Channel.isInvalid(channel)) {
            errors.add(String.format("Invalid channel: %s", channel));
        }
        return errors;
    }
}
