package org.motechproject.ananya.referencedata.flw.service;

import org.motechproject.ananya.referencedata.flw.request.LocationRequest;

public class FrontLineWorkerRequest {
    private String guid;
    private String name;
    private String designation;
    private LocationRequest location = new LocationRequest();
    private String verificationStatus;
    private String reason;

    public FrontLineWorkerRequest( String guid, String msisdn, String name, String designation, LocationRequest location, String verificationStatus, String reason) {
        this.guid = guid;
        this.name = name;
        this.designation = designation;
        this.location = location;
        this.verificationStatus = verificationStatus;
        this.reason = reason;
    }
}
