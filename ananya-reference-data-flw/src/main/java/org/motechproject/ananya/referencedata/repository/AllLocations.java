package org.motechproject.ananya.referencedata.repository;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Location> getAll() {
        return template.loadAll(Location.class);
    }

    public Location getFor(String district, String block, String panchayat) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Location.class);

        criteria.add(Restrictions.eq("district", district).ignoreCase());
        criteria.add(Restrictions.eq("block", block).ignoreCase());
        criteria.add(Restrictions.eq("panchayat", panchayat).ignoreCase());

        List locationList = template.findByCriteria(criteria);
        return locationList.isEmpty() ? null : (Location) locationList.get(0);
    }

    public void addAll(List<Location> locations) {
        template.saveOrUpdateAll(locations);
    }
}