package org.motechproject.ananya.referencedata.flw.request;

import org.motechproject.importer.annotation.ColumnName;

public class FrontLineWorkerRequest {
    private String msisdn;
    private String name;
    private String designation;
    private LocationRequest location = new LocationRequest();

    public FrontLineWorkerRequest() {
    }

    public FrontLineWorkerRequest(String msisdn, String name, String designation, LocationRequest location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation;
        this.location = location;
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

    public LocationRequest getLocation() {
        return location;
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

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    @ColumnName(name = "district")
    public void setDistrict(String district) {
        location.setDistrict(district);
    }

    @ColumnName(name = "block")
    public void setBlock(String block) {
        location.setBlock(block);
    }

    @ColumnName(name = "panchayat")
    public void setPanchayat(String panchayat) {
        location.setPanchayat(panchayat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrontLineWorkerRequest)) return false;

        FrontLineWorkerRequest that = (FrontLineWorkerRequest) o;

        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return msisdn != null ? msisdn.hashCode() : 0;
    }

    public String toCSV() {
        return "\"" + msisdn + "\"" +  "," + "\"" +  name + "\"" +  "," + "\"" +  designation + "\"" +  "," + "\"" +  location.getDistrict() + "\"" +  "," + "\"" +  location.getBlock() + "\"" +  "," + "\"" +  location.getPanchayat() + "\"";
    }
}