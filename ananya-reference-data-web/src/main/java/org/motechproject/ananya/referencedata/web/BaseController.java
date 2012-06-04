package org.motechproject.ananya.referencedata.web;

import org.motechproject.ananya.referencedata.response.ExceptionResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class BaseController {
    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    String handleException(final Exception exception, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        return new ExceptionResponse(exception.getMessage(), stringWriter.toString()).toString();
    }
}
