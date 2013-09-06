package org.motechproject.ananya.referencedata.flw.domain;

import org.junit.Assert;
import org.junit.Test;

public class NewMsisdnTest {
    @Test
    public void shouldFormatMsisdn() {
        Long expectedMsisdn = 911234567890L;
        Assert.assertEquals(expectedMsisdn, new NewMsisdn("1234567890", null).msisdn());
        Assert.assertEquals(expectedMsisdn, new NewMsisdn("911234567890", null).msisdn());
        Assert.assertNull(new NewMsisdn("123456789", null).msisdn());
    }
}
