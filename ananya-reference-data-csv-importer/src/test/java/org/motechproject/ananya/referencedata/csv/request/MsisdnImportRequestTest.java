package org.motechproject.ananya.referencedata.csv.request;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MsisdnImportRequestTest {
    @Test
    public void shouldCheckIfRequestIsForChangeMsisdn() {
        MsisdnImportRequest msisdnImportRequest = new MsisdnImportRequest("1234567890", "1234567891", null);
        assertTrue(msisdnImportRequest.isChangeMsisdn());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", null, null);
        assertFalse(msisdnImportRequest.isChangeMsisdn());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", " ", null);
        assertFalse(msisdnImportRequest.isChangeMsisdn());
    }

    @Test
    public void shouldCheckIfRequestIsForUpdateAlternateContactNumber() {
        MsisdnImportRequest msisdnImportRequest = new MsisdnImportRequest("1234567890", null, "1234567891");
        assertTrue(msisdnImportRequest.isUpdateAlternateContactNumber());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", null, null);
        assertFalse(msisdnImportRequest.isUpdateAlternateContactNumber());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", null, " ");
        assertFalse(msisdnImportRequest.isUpdateAlternateContactNumber());
    }

    @Test
    public void shouldReturnMsisdnAsLong() {
        MsisdnImportRequest msisdnImportRequest = new MsisdnImportRequest("1234567890", null, "1234567891");
        assertEquals(911234567890L, (long) msisdnImportRequest.msisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest("911234567890", null, "1234567891");
        assertEquals(911234567890L, (long) msisdnImportRequest.msisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest("001234567890", null, "1234567891");
        assertEquals(911234567890L, (long) msisdnImportRequest.msisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest(null, null, "1234567891");
        assertNull(msisdnImportRequest.msisdnAsLong());
    }

    @Test
    public void shouldReturnNewMsisdnAsLong() {
        MsisdnImportRequest msisdnImportRequest = new MsisdnImportRequest("1234567890", "1234567891", "1234567892");
        assertEquals(911234567891L, (long) msisdnImportRequest.newMsisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", "911234567891", "1234567892");
        assertEquals(911234567891L, (long) msisdnImportRequest.newMsisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", "001234567891", "1234567892");
        assertEquals(911234567891L, (long) msisdnImportRequest.newMsisdnAsLong());

        msisdnImportRequest = new MsisdnImportRequest("1234567890", null, "1234567892");
        assertNull(msisdnImportRequest.newMsisdnAsLong());
    }
}
