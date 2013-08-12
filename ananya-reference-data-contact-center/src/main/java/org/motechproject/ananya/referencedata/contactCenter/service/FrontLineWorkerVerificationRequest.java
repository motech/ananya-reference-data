package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FrontLineWorkerVerificationRequest {

    private ChangeMsisdnRequest changeMsisdnRequest;
    private VerificationStatus verificationStatus;
    private Long msisdn;
    private Long alternateContactNumber;
    private UUID flwId;
    private String name;
    private Designation designation;
    private LocationRequest location;
    private String reason;

    public FrontLineWorkerVerificationRequest(UUID flwId, Long msisdn, Long alternateContactNumber, VerificationStatus verificationStatus, String name, Designation designation, LocationRequest location, String reason, ChangeMsisdnRequest changeMsisdnRequest) {
        this.verificationStatus = verificationStatus;
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber;
        this.flwId = flwId;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.reason = reason;
        this.changeMsisdnRequest = changeMsisdnRequest;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public Long getAlternateContactNumber() {
        return alternateContactNumber;
    }

    public UUID getFlwId() {
        return flwId;
    }

    public String getName() {
        return name;
    }

    public Designation getDesignation() {
        return designation;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public boolean isDummyFlwId() {
        return FrontLineWorker.DEFAULT_UUID.equals(flwId);
    }

    public ChangeMsisdnRequest getChangeMsisdnRequest() {
        return changeMsisdnRequest;
    }

    public boolean hasMsisdnChange() {
        return changeMsisdnRequest != null && isNotBlank(changeMsisdnRequest.getMsisdn());
    }

    boolean duplicateMsisdnExists() {
        return hasMsisdnChange() && getChangeMsisdnRequest().flwIdInDb();
    }

    String duplicateFlwId() {
        if (duplicateMsisdnExists())
            return getChangeMsisdnRequest().getFlwId();
        throw new IllegalStateException("Duplicate flw does not exist");
    }
}
