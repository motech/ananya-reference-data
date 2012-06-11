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
    private Gson gson;

    public static class Response {
        public final int statusCode;
        public final Object body;

        public Response(int statusCode, Object body) {
            this.body = body;
            this.statusCode = statusCode;
        }
    }

    public JsonHttpClient() {
        this.gson = new Gson();
    }

    public Response post(final String uri, final Object req) throws IOException {
        return post(uri, gson.toJson(req), null);
    }

    public Response post(final String uri, final Object req, Type responseClass) throws IOException {
        return post(uri, gson.toJson(req), responseClass);
    }

    public Response post(final String uri, final String json, Type responseClass) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(uri) {
            {
                setRequestEntity(new StringRequestEntity(json, "application/json", null));
            }
        };
        httpClient.executeMethod(postMethod);
        
        int statusCode = postMethod.getStatusCode();
        return responseClass != null ? new Response(statusCode, gson.fromJson(postMethod.getResponseBodyAsString(), responseClass)) : new Response(statusCode, postMethod.getResponseBodyAsString());
    }
}
