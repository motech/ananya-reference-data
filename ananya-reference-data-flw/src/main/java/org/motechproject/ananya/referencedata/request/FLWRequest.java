package org.motechproject.ananya.referencedata.request;

public class FLWRequest {
    private String msisdn;
    private String name;
    private String designation;
    private String district;
    private String block;
    private String panchayat;
    
    public FLWRequest(){
    }

    public FLWRequest(String msisdn, String name, String designation, String district, String block, String panchayat) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation;
        this.district = district;
        this.block = block;
        this.panchayat = panchayat;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
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

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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
}
