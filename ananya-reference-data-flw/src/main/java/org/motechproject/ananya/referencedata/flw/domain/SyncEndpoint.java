package org.motechproject.ananya.referencedata.flw.domain;

public class SyncEndpoint {
    private String url;
    private String apiKeyValue;
    private String apiKeyName;

    public SyncEndpoint(String url, String apiKeyName, String apiKeyValue) {
        this.url = url;
        this.apiKeyName = apiKeyName;
        this.apiKeyValue = apiKeyValue;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }
}
