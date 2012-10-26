package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.web.message.converters.annotations.CSVEntity;

import java.util.ArrayList;
import java.util.Collection;

@CSVEntity
public class LocationResponseList extends ArrayList<LocationResponse> {
    public LocationResponseList(Collection<? extends LocationResponse> c) {
        super(c);
    }
}
