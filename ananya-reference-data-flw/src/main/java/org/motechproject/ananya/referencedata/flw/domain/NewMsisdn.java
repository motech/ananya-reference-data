package org.motechproject.ananya.referencedata.flw.domain;

import org.motechproject.ananya.referencedata.flw.utils.PhoneNumber;

public class NewMsisdn {
    private String msisdn;
    private String flwId;

    public NewMsisdn(String msisdn, String flwId) {
        this.msisdn = msisdn;
        this.flwId = flwId;
    }

    public Long msisdn() {
        return PhoneNumber.formatPhoneNumber(msisdn);
    }
}
