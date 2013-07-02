package org.motechproject.ananya.referencedata.contactCenter.service;

import org.motechproject.ananya.referencedata.flw.domain.Designation;
import org.motechproject.ananya.referencedata.flw.domain.VerificationStatus;
import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

import java.util.UUID;

public class FrontLineWorkerVerificationRequest {

    private VerificationStatus verificationStatus;
    private Long msisdn;
    private UUID flwId;
    private String name;
    private Designation designation;
    private LocationRequest location;
    private String reason;
    private final static UUID DUMMY_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public FrontLineWorkerVerificationRequest(UUID flwId, Long msisdn, VerificationStatus verificationStatus, String name, Designation designation, LocationRequest location, String reason) {
        this.verificationStatus = verificationStatus;
        if (verificationStatus == VerificationStatus.SUCCESS && location != null)
            location.handleMissingState();
        this.msisdn = msisdn;
        this.flwId = flwId;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.reason = reason;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public Long getMsisdn() {
        return msisdn;
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
        return DUMMY_UUID.equals(flwId);
    }
}
