package org.motechproject.ananya.referencedata.flw.repository;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class AllLocations {

    @Autowired
    private DataAccessTemplate template;

    public AllLocations() {
    }

    public void add(Location location) {
        template.save(location);
    }

    public void update(Location location) {
        template.update(location);
    }

    public Location getFor(String district, String block, String panchayat) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);

        criteria.add(Restrictions.eq("district", district).ignoreCase());
        criteria.add(Restrictions.eq("block", block).ignoreCase());
        criteria.add(Restrictions.eq("panchayat", panchayat).ignoreCase());

        List locationList = template.findByCriteria(criteria);
        return locationList.isEmpty() ? null : (Location) locationList.get(0);
    }

    public void addAll(Set<Location> locations) {
        template.saveOrUpdateAll(locations);
    }

    public List<Location> getAllForStatus(LocationStatus status) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);
        criteria.add(Restrictions.eq("status", status.name()).ignoreCase());
        return template.findByCriteria(criteria);
    }
}