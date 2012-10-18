package org.motechproject.ananya.referencedata.web;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class Beneficiary {

    @XmlElement
    @JsonProperty
    private String name;

    public Beneficiary() {
    }

    public Beneficiary(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
