package org.motechproject.ananya.referencedata.flw.request;

import org.junit.Test;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeMsisdnRequestTest {

    @Test
    public void shouldCheckIfFlwIdIsInDb() {
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(null, null);
        assertFalse(changeMsisdnRequest.flwIdInDb());

        changeMsisdnRequest.setFlwId(FrontLineWorker.DEFAULT_UUID_STRING);
        assertFalse(changeMsisdnRequest.flwIdInDb());

        changeMsisdnRequest.setFlwId(UUID.randomUUID().toString());
        assertTrue(changeMsisdnRequest.flwIdInDb());
    }
}
