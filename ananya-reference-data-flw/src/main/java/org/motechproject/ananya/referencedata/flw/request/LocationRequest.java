package org.motechproject.ananya.referencedata.flw.request;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
public class LocationRequest {
    @XmlElement
    @JsonProperty
    private String district;
    @XmlElement
    @JsonProperty
    private String block;
    @XmlElement
    @JsonProperty
    private String panchayat;

    public LocationRequest() {
    }

    public LocationRequest(String district, String block, String panchayat) {
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

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public String toCSV() {
        return "\"" + district + "\"" + "," + "\"" + block + "\"" + "," +  "\"" + panchayat + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationRequest that = (LocationRequest) o;

        if (block != null ? !block.equals(that.block) : that.block != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
        if (panchayat != null ? !panchayat.equals(that.panchayat) : that.panchayat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = district != null ? district.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        result = 31 * result + (panchayat != null ? panchayat.hashCode() : 0);
        return result;
    }
}
