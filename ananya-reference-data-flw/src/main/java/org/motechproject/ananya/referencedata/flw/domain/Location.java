package org.motechproject.ananya.referencedata.flw.domain;

import liquibase.util.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location extends BaseEntity {
    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "panchayat")
    private String panchayat;

    @Column(name = "status")
    private String status;

    public Location() {
    }

    public Location(String district, String block, String panchayat, String status) {
        this.district = StringUtils.trimToEmpty(district);
        this.block = StringUtils.trimToEmpty(block);
        this.panchayat = StringUtils.trimToEmpty(panchayat);
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

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (block != null ? !block.equalsIgnoreCase(location.block) : location.block != null) return false;
        if (district != null ? !district.equalsIgnoreCase(location.district) : location.district != null) return false;
        if (panchayat != null ? !panchayat.equalsIgnoreCase(location.panchayat) : location.panchayat != null) return false;
        if (status != null ? !status.equalsIgnoreCase(location.status) : location.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "district=\"" + district + '"' +
                ", block=\"" + block + '"' +
                ", panchayat=\"" + panchayat + '"' +
                '}';
    }
}
