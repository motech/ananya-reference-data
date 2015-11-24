package org.motechproject.ananya.referencedata.flw.repository;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.motechproject.ananya.referencedata.flw.domain.LocationFilename;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AllLocationFilename {

	   @Autowired
	    private DataAccessTemplate template;

	    public AllLocationFilename() {
			
		}
	    
	    public void add(LocationFilename locationFilename) {
	        template.save(locationFilename);
	    }
	
	    @Transactional(readOnly = true)
	    public List<LocationFilename> getFor(String uuid) {
	        DetachedCriteria criteria = DetachedCriteria.forClass(LocationFilename.class);
	        criteria.add(Restrictions.eq("uuid", uuid).ignoreCase());
	        @SuppressWarnings("unchecked")
			List<LocationFilename> locationList = template.findByCriteria(criteria);
	        return locationList.isEmpty() ? null :  locationList;
	    }

	
	
}
