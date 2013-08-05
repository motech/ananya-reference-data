package org.motechproject.ananya.referencedata.flw.domain;

import liquibase.util.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location extends BaseEntity implements Cloneable {
    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "panchayat")
    private String panchayat;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LocationStatus status;

    @Column(name = "state")
    private String state;

    @ManyToOne
    @JoinColumn(name = "alternate_location")
    private Location alternateLocation;

    public Location() {
    }

    public Location(String district, String block, String panchayat, String state, LocationStatus status, Location alternateLocation) {
        this.alternateLocation = alternateLocation;
        this.district = WordUtils.capitalizeFully(StringUtils.trimToEmpty(district));
        this.block = WordUtils.capitalizeFully(StringUtils.trimToEmpty(block));
        this.panchayat = WordUtils.capitalizeFully(StringUtils.trimToEmpty(panchayat));
        this.state = WordUtils.capitalizeFully(StringUtils.trimToEmpty(state));
        this.status = status;
    }

    public String getDistrict() {
        return district;
    }

    public String getBlock() {
        return block;
    }

    public String getPanchayat() {
        return panchayat;
    }

    public LocationStatus getStatus() {
        return status;
    }

    public Location getAlternateLocation() {
        return alternateLocation;
    }

    public void setStatus(LocationStatus status) {
        this.status = status;
    }

    public void setAlternateLocation(Location alternateLocation) {
        this.alternateLocation = alternateLocation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isValidatedLocation() {
        return status == LocationStatus.VALID;
    }

    public boolean isInvalid() {
        return status == LocationStatus.INVALID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (alternateLocation != null ? !alternateLocation.equals(location.alternateLocation) : location.alternateLocation != null)
            return false;
        if (block != null ? !block.equalsIgnoreCase(location.block) : location.block != null) return false;
        if (district != null ? !district.equalsIgnoreCase(location.district) : location.district != null) return false;
        if (panchayat != null ? !panchayat.equalsIgnoreCase(location.panchayat) : location.panchayat != null)
            return false;
        if (status != null ? !status.equals(location.status) : location.status != null) return false;
        if (state != null ? !state.equalsIgnoreCase(location.state) : location.state != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (alternateLocation != null ? alternateLocation.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}