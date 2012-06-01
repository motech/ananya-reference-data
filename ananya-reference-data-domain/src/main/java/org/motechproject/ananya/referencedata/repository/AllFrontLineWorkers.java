package org.motechproject.ananya.referencedata.repository;

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
}