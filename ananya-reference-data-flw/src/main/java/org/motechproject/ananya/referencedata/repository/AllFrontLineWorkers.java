package org.motechproject.ananya.referencedata.repository;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.referencedata.domain.FrontLineWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
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
}