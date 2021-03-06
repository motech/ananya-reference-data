package org.motechproject.ananya.referencedata.web.utils;

import org.motechproject.ananya.referencedata.contactCenter.request.FrontLineWorkerVerificationWebRequest;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

public class FrontLineWorkerVerificationWebRequestBuilder {
    String flwId;
    String msisdn;
    String verificationStatus;
    String reason;
    String name;
    String designation;
    String district;
    String block;
    String panchayat;
    String state;
    private boolean failedVerification;
    private String alternateContactNumber;
    private ChangeMsisdnRequest changeMsisdn;

    public FrontLineWorkerVerificationWebRequestBuilder withFlwId(String flwId) {
        this.flwId = flwId;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withReason(String reason) {
        this.reason = reason;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withDesignation(String designation) {
        this.designation = designation;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withBlock(String block) {
        this.block = block;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withState(String state) {
        this.state = state;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withPanchayat(String panchayat) {
        this.panchayat = panchayat;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withDefaults() {
        return this;
    }

    public FrontLineWorkerVerificationWebRequest build() {
        return new FrontLineWorkerVerificationWebRequest(flwId, msisdn, alternateContactNumber, verificationStatus, name, designation, failedVerification ? null : new LocationRequest(district, block, panchayat, state), reason, changeMsisdn);
    }

    public FrontLineWorkerVerificationWebRequestBuilder withFailedVerification(boolean failedVerification) {
        this.failedVerification = failedVerification;
        return this;
    }

    public FrontLineWorkerVerificationWebRequestBuilder withChangeMsisdn(ChangeMsisdnRequest changeMsisdn) {
        this.changeMsisdn = changeMsisdn;
        return this;
    }
}
