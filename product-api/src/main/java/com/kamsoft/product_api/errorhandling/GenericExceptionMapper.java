package com.kamsoft.product_api.errorhandling;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    //http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/#21_Checkedbusiness_exceptions
    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionMapper.class);
    
    @Override
    public Response toResponse(Throwable ex) {

        ErrorMessage errorMessage = new ErrorMessage();     
        setHttpStatus(ex, errorMessage);
        errorMessage.setCode(500);
        errorMessage.setMessage(ex.getMessage());

        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        LOG.error(ex.toString(), ex);
        errorMessage.setDeveloperMessage(errorStackTrace.toString());
        //errorMessage.setDeveloperMessage(ex.toString());

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
        if(ex instanceof WebApplicationException ) {
            errorMessage.setStatus(((WebApplicationException)ex).getResponse().getStatus());
        } else {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); //defaults to internal server error 500
        }
    }
}