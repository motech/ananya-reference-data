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

    public Location() {
    }

    public Location(String district, String block, String panchayat) {
        this.district = StringUtils.trimToEmpty(district);
        this.block = StringUtils.trimToEmpty(block);
        this.panchayat = StringUtils.trimToEmpty(panchayat);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (block != null ? !block.equalsIgnoreCase(location.block) : location.block != null) return false;
        if (district != null ? !district.equalsIgnoreCase(location.district) : location.district != null) return false;
        if (panchayat != null ? !panchayat.equalsIgnoreCase(location.panchayat) : location.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
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
