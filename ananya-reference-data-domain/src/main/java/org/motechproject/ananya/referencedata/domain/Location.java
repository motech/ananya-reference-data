package org.motechproject.ananya.referencedata.domain;

import javax.persistence.*;

@Entity
@Table(name = "location_dimension")
public class Location {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "district")
    private String district;

    @Column(name = "block")
    private String block;

    @Column(name = "panchayat")
    private String panchayat;

    @Column(name = "districtCode")
    private Integer districtCode;

    @Column(name = "blockCode")
    private Integer blockCode;

    @Column(name = "panchayatCode")
    private Integer panchayatCode;

    private String locationId;

    public Location() {
    }

    public Location(String district, String block, String panchayat, Integer districtCode, Integer blockCode, Integer panchayatCode) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
        this.districtCode = districtCode;
        this.blockCode = blockCode;
        this.panchayatCode = panchayatCode;
        locationId = "S01" + "D" + prependZeros(districtCode) + "B" + prependZeros(blockCode) + "V" + prependZeros(panchayatCode);
    }

    public Location(String district, String block, String panchayat) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
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

    public Integer getDistrictCode() {
        return districtCode;
    }

    public Integer getBlockCode() {
        return blockCode;
    }

    public Integer getPanchayatCode() {
        return panchayatCode;
    }

    public void updateLocationCode(Integer districtCode, Integer blockCode, Integer panchayatCode) {
        this.districtCode = districtCode;
        this.blockCode = blockCode;
        this.panchayatCode = panchayatCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (block != null ? !block.equals(location.block) : location.block != null) return false;
        if (district != null ? !district.equals(location.district) : location.district != null) return false;
        if (panchayat != null ? !panchayat.equals(location.panchayat) : location.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }

    private String prependZeros(int code) {
        return String.format("%03d", code);
    }
}
