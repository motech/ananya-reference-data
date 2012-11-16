package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

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

    @Column(name = "flw_id")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID flwId;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "reason")
    private String reason;

    public FrontLineWorker() {
    }

    public FrontLineWorker(UUID flwId) {
        this.flwId = flwId;
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation == null ? null : designation.name();
        this.location = location;
        this.flwId = UUID.randomUUID();
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location, UUID flwId, VerificationStatus verificationStatus, String reason) {
        this.flwId = flwId;
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation == null ? null : designation.name();
        this.location = location;
        this.verificationStatus = verificationStatus.name();
        this.reason = reason;
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

    public UUID getFlwId() {
        return flwId;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus.name();
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

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean hasBeenVerified() {
        return verificationStatus != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrontLineWorker that = (FrontLineWorker) o;

        if (designation != null ? !designation.equals(that.designation) : that.designation != null) return false;
        if (flwId != null ? !flwId.equals(that.flwId) : that.flwId != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (verificationStatus != null ? !verificationStatus.equals(that.verificationStatus) : that.verificationStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msisdn != null ? msisdn.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (designation != null ? designation.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (flwId != null ? flwId.hashCode() : 0);
        result = 31 * result + (verificationStatus != null ? verificationStatus.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}