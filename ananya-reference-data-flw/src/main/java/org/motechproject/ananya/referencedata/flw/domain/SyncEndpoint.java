package org.motechproject.ananya.referencedata.flw.domain;

public class SyncEndpoint {
    private String url;
    private String apiKey;
    public static final String API_KEY = "apiKey";

    public SyncEndpoint(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }
}
