package org.motechproject.ananya.referencedata.flw.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class LocationContract implements Serializable {

    private String state;
    private String district;
    private String block;
    private String panchayat;

    public LocationContract(String state, String district, String block, String panchayat) {
        this.state = state;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getState() {
        return state;
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
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
