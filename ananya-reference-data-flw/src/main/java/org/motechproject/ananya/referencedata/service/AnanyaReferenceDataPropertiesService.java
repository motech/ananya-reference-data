package org.motechproject.ananya.referencedata.service;

import org.motechproject.ananya.referencedata.repository.AllAnanyaReferenceDataProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnanyaReferenceDataPropertiesService {
    private AllAnanyaReferenceDataProperties allDbProperties;

    @Autowired
    public AnanyaReferenceDataPropertiesService(AllAnanyaReferenceDataProperties allDbProperties) {
        this.allDbProperties = allDbProperties;
    }

    public boolean isSyncOn() {
        return "on".equalsIgnoreCase(allDbProperties.getForSyncSwitch());
    }
}
