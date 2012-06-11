package org.motechproject.ananya.referencedata.domain;

import javax.persistence.*;

@Entity
@Table(name = "ananya_reference_data_property")
public class AnanyaReferenceDataProperty {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    public AnanyaReferenceDataProperty() {
    }

    public AnanyaReferenceDataProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
