package org.motechproject.ananya.referencedata.flw.repository;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllLocations {

    @Autowired
    private DataAccessTemplate template;

    public AllLocations() {
    }

    public void add(Location location) {
        template.save(location);
    }

    @Transactional
    public void update(final Location location) {
        if(!location.getStatus().isInvalid()){
            template.update(location);
            return;
        }
        List<Location> locationsToUpdate = getForAlternateLocation(location);
        List<Location> updatedLocations = updateAlternateLocation(location, locationsToUpdate);
        template.saveOrUpdateAll(updatedLocations);
    }

    @Transactional(readOnly = true)
    public Location getFor(String district, String block, String panchayat) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);

        criteria.add(Restrictions.eq("district", district).ignoreCase());
        criteria.add(Restrictions.eq("block", block).ignoreCase());
        criteria.add(Restrictions.eq("panchayat", panchayat).ignoreCase());

        List locationList = template.findByCriteria(criteria);
        return locationList.isEmpty() ? null : (Location) locationList.get(0);
    }

    @Transactional(readOnly = true)
    public List<Location> getForStatuses(LocationStatus... statuses) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);
        criteria.add(Restrictions.in("status", statuses));
        return template.findByCriteria(criteria);
    }

    private List<Location> getForAlternateLocation(Location location) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);
        criteria.add(Restrictions.eq("alternateLocation", location));
        return template.findByCriteria(criteria);
    }

    private List<Location> updateAlternateLocation(final Location location, List<Location> locationsToUpdate) {
        return (ArrayList<Location>) CollectionUtils.collect(locationsToUpdate, new Transformer() {
            @Override
            public Object transform(Object input) {
                Location locationToUpdate = (Location) input;
                locationToUpdate.setAlternateLocation(location.getAlternateLocation());
                return locationToUpdate;
            }
        });
    }
}