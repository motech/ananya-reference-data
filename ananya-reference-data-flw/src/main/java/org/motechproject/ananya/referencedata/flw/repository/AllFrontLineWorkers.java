package org.motechproject.ananya.referencedata.flw.repository;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.flw.domain.FrontLineWorker;
import org.motechproject.ananya.referencedata.flw.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AllFrontLineWorkers {

    @Autowired
    private DataAccessTemplate template;

    public void add(FrontLineWorker frontLineWorker) {
        template.save(frontLineWorker);
    }

    public List<FrontLineWorker> getAll() {
        return template.loadAll(FrontLineWorker.class);
    }

    public List<FrontLineWorker> getByMsisdn(Long msisdn) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorker.class);

        criteria.add(Restrictions.eq("msisdn", msisdn));

        List frontLineWorkerList = template.findByCriteria(criteria);
        return frontLineWorkerList;
    }

    public void createOrUpdate(FrontLineWorker frontLineWorker) {
        template.saveOrUpdate(frontLineWorker);
    }

    public void createOrUpdateAll(List<FrontLineWorker> frontLineWorkers) {
        template.saveOrUpdateAll(frontLineWorkers);
    }

    public FrontLineWorker getByFlwId(UUID flwId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorker.class);

        criteria.add(Restrictions.eq("flwId", flwId));

        List frontLineWorkerList = template.findByCriteria(criteria);
        return frontLineWorkerList.size() == 0 ? null : (FrontLineWorker) frontLineWorkerList.get(0);
    }

    public List<FrontLineWorker> getForLocation(Location location) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorker.class);
        criteria.add(Restrictions.eq("location", location));
        return (List<FrontLineWorker>) template.findByCriteria(criteria);
    }

    public List<FrontLineWorker> getByMsisdnWithStatus(Long msisdn) {
        DetachedCriteria criteria = DetachedCriteria.forClass(FrontLineWorker.class);
        criteria.add(Restrictions.eq("msisdn", msisdn));
        criteria.add(Restrictions.isNotNull("verificationStatus"));

        return template.findByCriteria(criteria);
    }
}