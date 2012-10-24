package org.motechproject.ananya.referencedata.flw.request;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.*;

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

    @JsonIgnore
    @XmlTransient
    private String status = "NOT VERIFIED";

    public LocationRequest() {
    }

    public LocationRequest(String district, String block, String panchayat, String status) {
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
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

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setPanchayat(String panchayat) {
        this.panchayat = panchayat;
    }

    public void setStatus(String status) {
        this.status = status;
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
