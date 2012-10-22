package org.motechproject.ananya.referencedata.flw.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "front_line_worker")
public class FrontLineWorker extends BaseEntity {

    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "name")
    private String name;

    @Column(name = "designation")
    private String designation;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "flw_guid")
    private String flwGuid;

    @Column(name="verification_status")
    private String verificationStatus;

    @Column(name="reason")
    private String reason;

    public FrontLineWorker() {
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location, String flwGuid, VerificationStatus verificationStatus, String reason) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation == null ? null : designation.name();
        this.location = location;
        this.flwGuid = flwGuid;
        this.verificationStatus = verificationStatus.name();
        this.reason = reason;
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation == null ? null : designation.name();
        this.location = location;
        this.flwGuid = UUID.randomUUID().toString();
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public Location getLocation() {
        return location;
    }

    public String getFlwGuid() {
        return flwGuid;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation == null ? null : designation.name();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus.name();
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}