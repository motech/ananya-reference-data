package org.motechproject.ananya.referencedata.repository;

import org.motechproject.ananya.referencedata.domain.FLWData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllFLWData {

    @Autowired
    private DataAccessTemplate template;

    public void add(FLWData flwData) {
        template.save(flwData);
    }

    public List<FLWData> getAll() {
        return template.loadAll(FLWData.class);
    }
}