package com.kamsoft.product_api.errorhandling;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
@Singleton
public class AppExceptionMapper implements ExceptionMapper<AppException>{

    @Override
    public Response toResponse(AppException ex) {
            return Response.status(ex.getStatus())
                    .entity(new ErrorMessage(ex))
                    .type(MediaType.APPLICATION_JSON).
                    build();
    }

}
