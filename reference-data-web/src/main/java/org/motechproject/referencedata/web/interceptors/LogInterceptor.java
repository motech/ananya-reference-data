package org.motechproject.referencedata.web.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class LogInterceptor extends HandlerInterceptorAdapter {
    private final static Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append("Request START [uri=" + request.getRequestURI() + "|");
        builder.append("Parameters={");

        Enumeration<String> requestKeys = request.getParameterNames();
        while (requestKeys.hasMoreElements()) {
            String key = requestKeys.nextElement();
            builder.append(key + "=>" + request.getParameter(key) + ",");
        }
        builder.append("}]");
        LOG.info(builder.toString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.info("Request END [uri=" + request.getRequestedSessionId());
    }
}
