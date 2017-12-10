package com.kamsoft.product_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthCheckResource {

    @GET
    public Response getHealthStatus() {
     // Return a 200 for ELB health checks.
        return Response.status(200).build();
        
    }
    
}
