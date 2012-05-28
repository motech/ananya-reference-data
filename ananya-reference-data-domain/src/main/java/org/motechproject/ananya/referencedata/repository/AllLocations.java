package org.motechproject.ananya.referencedata.repository;


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
}
