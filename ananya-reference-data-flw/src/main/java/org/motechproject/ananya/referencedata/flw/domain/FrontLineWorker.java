package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "front_line_worker")
public class FrontLineWorker extends BaseEntity implements Cloneable {

    public static final String FLW_ID_FORMAT = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public final static String DEFAULT_UUID_STRING = "11111111-1111-1111-1111-111111111111";
    public final static UUID DEFAULT_UUID = UUID.fromString(DEFAULT_UUID_STRING);
    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "alternate_contact_number")
    private Long alternateContactNumber;

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

    @Transient
    private NewMsisdn newMsisdn;

    public FrontLineWorker() {
    }

    public FrontLineWorker(UUID flwId) {
        this.flwId = flwId;
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location, String verificationStatus) {
        this(msisdn, null, name, designation, location, verificationStatus, UUID.randomUUID(), null);
    }

    public FrontLineWorker(Long msisdn, Long alternateContactNumber, String name, Designation designation, Location location, String verificationStatus, UUID flwId, String reason) {
        this.flwId = flwId;
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation == null ? null : designation.name();
        this.location = location;
        this.verificationStatus = verificationStatus;
        this.reason = reason;
        this.alternateContactNumber = alternateContactNumber;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public Long getAlternateContactNumber() {
        return alternateContactNumber;
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

    public void setAlternateContactNumber(Long alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus == null ? null : verificationStatus.name();
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
    public FrontLineWorker clone() {
        try {
            return (FrontLineWorker) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
        if (alternateContactNumber != null ? !alternateContactNumber.equals(that.alternateContactNumber) : that.alternateContactNumber != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        if (verificationStatus != null ? !verificationStatus.equals(that.verificationStatus) : that.verificationStatus != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msisdn != null ? msisdn.hashCode() : 0;
        result = 31 * result + (alternateContactNumber != null ? alternateContactNumber.hashCode() : 0);
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

    public void setNewMsisdn(NewMsisdn newMsisdn) {
        this.newMsisdn = newMsisdn;
    }

    public void updateToNewMsisdn() {
        if (msisdnChange())
            setMsisdn(newMsisdn.msisdn());
    }

    public boolean msisdnChange() {
        return newMsisdn != null;
    }

    public NewMsisdn getNewMsisdn() {
        return newMsisdn;
    }
}