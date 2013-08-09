package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.ChangeMsisdnRequest;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import java.util.UUID;

public class FrontLineWorkerVerificationRequest {

    private final ChangeMsisdnRequest changeMsisdn;
    private VerificationStatus verificationStatus;
    private Long msisdn;
    private Long alternateContactNumber;
    private UUID flwId;
    private String name;
    private Designation designation;
    private LocationRequest location;
    private String reason;

    public FrontLineWorkerVerificationRequest(UUID flwId, Long msisdn, Long alternateContactNumber, VerificationStatus verificationStatus, String name, Designation designation, LocationRequest location, String reason, ChangeMsisdnRequest changeMsisdn) {
        this.verificationStatus = verificationStatus;
        this.msisdn = msisdn;
        this.alternateContactNumber = alternateContactNumber;
        this.flwId = flwId;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.reason = reason;
        this.changeMsisdn = changeMsisdn;
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

    public boolean isDummyFlwId(){
        return FrontLineWorker.DEFAULT_UUID.equals(flwId);
    }

    public ChangeMsisdnRequest getChangeMsisdn() {
        return changeMsisdn;
    }
}
