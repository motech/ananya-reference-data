package org.motechproject.ananya.referencedata.domain;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.sql.Timestamp;

@MappedSuperclass
public class BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "last_modified")
    protected Timestamp lastModified;

    public Integer getId() {
        return id;
    }

    public DateTime getLastModified() {
        return new DateTime(lastModified.getTime());
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = new Timestamp(lastModified.getMillis());
    }
}
