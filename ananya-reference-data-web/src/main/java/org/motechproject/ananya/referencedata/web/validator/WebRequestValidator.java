package org.motechproject.ananya.referencedata.web.validator;

import org.motechproject.ananya.referencedata.web.domain.Channel;
import org.springframework.stereotype.Component;

@Component
public class WebRequestValidator {

    public ValidationResponse validateChannel(String channel) {
        ValidationResponse validationResponse = new ValidationResponse();
        if (Channel.isInvalid(channel)) {
            validationResponse.addError(String.format("Invalid channel: %s", channel));
        }
        return validationResponse;
    }
}
