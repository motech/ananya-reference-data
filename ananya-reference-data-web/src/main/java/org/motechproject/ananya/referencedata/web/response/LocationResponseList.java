package org.motechproject.ananya.referencedata.web.response;

import org.motechproject.export.annotation.ComponentTypeProvider;
import org.motechproject.web.message.converters.annotations.CSVEntity;

import java.util.ArrayList;
import java.util.Collection;

@CSVEntity
public class LocationResponseList extends ArrayList<LocationResponse> {
    private Class<?> clazz;

    public LocationResponseList(Collection<? extends LocationResponse> responses,Class<?> clazz) {
        super(responses);
        this.clazz = clazz;
    }

    @ComponentTypeProvider
    public Class<?> getType(){
        return clazz;
    }
}
