package org.motechproject.ananya.referencedata.contactCenter.request;

public class FrontLineWorkerWebRequest {
    private String guid;
    private String verificationStatus;
    private String reason;

    public FrontLineWorkerWebRequest(String guid, String verificationStatus, String reason) {
        this.guid = guid;
        this.verificationStatus = verificationStatus;
        this.reason = reason;
    }

    public String getGuid() {
        return guid;
    }

    public String getReason() {
        return reason;
    }


    public String getVerificationStatus() {
        return verificationStatus;
    }
}
