package org.motechproject.ananya.referencedata.service;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

@Component
public class JsonHttpClient {
    public String post(final String uri, final Object req) throws IOException {
        final Gson gson = new Gson();
        return (String) post(uri, gson.toJson(req), null);
    }

    public Object post(final String uri, final Object req, Type responseClass) throws IOException {
        final Gson gson = new Gson();
        return post(uri, gson.toJson(req), responseClass);
    }

    public Object post(final String uri, final String json, Type responseClass) throws IOException {
        final Gson gson = new Gson();
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(uri) {
            {
                setRequestEntity(new StringRequestEntity(json, "application/json", null));
            }
        };
        httpClient.executeMethod(postMethod);
        return responseClass != null ? gson.fromJson(postMethod.getResponseBodyAsString(), responseClass) : postMethod.getResponseBodyAsString();
    }
}
