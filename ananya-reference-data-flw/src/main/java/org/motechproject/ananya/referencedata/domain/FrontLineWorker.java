package org.motechproject.ananya.referencedata.domain;

import javax.persistence.*;

@Entity
@Table(name = "front_line_worker")
public class FrontLineWorker extends BaseEntity {

    @Column(name = "msisdn")
    private Long msisdn;

    @Column(name = "name")
    private String name;

    @Column(name = "designation")
    private String designation;

    @Column(name = "is_valid")
    private boolean isValid;

    @Column(name = "source")
    private Source source;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public FrontLineWorker(){
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation.name();
        this.location = location;
    }

    public boolean isValid() {
        return isValid;
    }

    public Source getSource() {
        return source;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public Location getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation.name();
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
