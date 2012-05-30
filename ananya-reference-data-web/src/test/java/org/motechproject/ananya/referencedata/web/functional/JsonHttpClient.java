package org.motechproject.ananya.referencedata.web.functional;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.lang.reflect.Type;

public class JsonHttpClient {
    private Type responseClass;

    public JsonHttpClient(Type responseClass) {
        this.responseClass = responseClass;
    }

    public Object post(final String uri, final Object req) throws IOException {
        final Gson gson = new Gson();
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(uri) {
            {
                setRequestEntity(new StringRequestEntity(gson.toJson(req), "application/json", null));
            }
        };
        httpClient.executeMethod(postMethod);
        return gson.fromJson(postMethod.getResponseBodyAsString(), responseClass);
    }
}
