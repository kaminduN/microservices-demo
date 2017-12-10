package com.kamsoft.product_api;

import java.util.Arrays;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.glassfish.jersey.server.ServerProperties;

import com.kamsoft.product_api.errorhandling.AppExceptionMapper;
import com.kamsoft.product_api.errorhandling.GenericExceptionMapper;
import com.kamsoft.product_api.filter.CORSResponseFilter;
import com.kamsoft.product_api.resources.HealthCheckResource;
import com.kamsoft.product_api.resources.ProductsResource;

/**
 * Main Entry point of the Jetty server
 *
 */
public class App 
{
	static final String APPLICATION_PATH = "/api";
	static final String CONTEXT_ROOT = "/";
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	static final int PORT = 8081;
	
    public static void main( String[] args ) throws Exception
    {
        LOG.info("Hello World!" );
        
        Log.setLog(new Slf4jLog());
        final Server jettyServer = new Server(PORT);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_ROOT);

        jettyServer.setHandler(context);
        
        // ??
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, APPLICATION_PATH + "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                ServerProperties.PROVIDER_CLASSNAMES,//"jersey.config.server.provider.classnames"
                String.join(";", Arrays.asList(
//                        CORSFilter.class.getCanonicalName(),
                        AppExceptionMapper.class.getCanonicalName(),// registering the exception mapper.
                        GenericExceptionMapper.class.getCanonicalName(),
                        CORSResponseFilter.class.getCanonicalName(), // CORS headers
                        HealthCheckResource.class.getCanonicalName(), // health check path
                        ProductsResource.class.getCanonicalName())));
        jerseyServlet.setInitParameter(
                "com.sun.jersey.spi.container.ContainerResponseFilters", 
                CORSResponseFilter.class.getCanonicalName()
        );
//        FilterHolder filterHolder = 
//        context.addFilter(org.eclipse.jetty.servlets.CrossOriginFilter.class, “/*”, EnumSet.of(DispatcherType.REQUEST));
//        filterHolder.setInitParameter(“allowedOrigins”, “*”);
        
        
        // add shutdown hook for Jetty server for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("shutdown hook invoked");
                    jettyServer.stop();

                } catch (Exception e) {
                    LOG.info("error stoping jetty server", e);
                }
            }
        });
        
        jettyServer.start();
        LOG.info("Server started on port " + PORT);
        jettyServer.join();
    }
}
