package org.motechproject.ananya.referencedata.flw.domain;

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

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "should_sync")
    private boolean shouldSync = false;

    public FrontLineWorker() {
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location) {
        this.msisdn = msisdn;
        this.name = name;
        this.designation = designation.name();
        this.location = location;
    }

    public FrontLineWorker(Long msisdn, String name, Designation designation, Location location, boolean shouldSync) {
        this(msisdn, name, designation, location);
        this.shouldSync = shouldSync;
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

    public boolean getShouldSync() {
        return shouldSync;
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

    public void setShouldSync(boolean shouldSync) {
        this.shouldSync = shouldSync;
    }
}
