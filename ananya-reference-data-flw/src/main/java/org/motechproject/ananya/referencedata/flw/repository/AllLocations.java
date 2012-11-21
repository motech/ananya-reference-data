package org.motechproject.ananya.referencedata.flw.repository;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public List<Location> getForStatuses(LocationStatus... statuses) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);
        criteria.add(Restrictions.in("status", statuses));
        return template.findByCriteria(criteria);
    }
}