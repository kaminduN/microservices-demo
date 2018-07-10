package com.kamsoft.api;


import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import static spark.Spark.get;
import static spark.Spark.delete;
import static spark.Spark.port;
import static spark.Spark.before;
import static spark.Spark.options;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.google.gson.Gson;
import com.kamsoft.api.dao.ContentDAO;
import com.kamsoft.api.dao.ContentS3DAO;
import com.kamsoft.api.dao.ContentS3Upload;


public class App {
    
    private static final Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        port(8080);

//        ContentDAO dao = new ContentS3DAO();
        LOG.info("Hello world");
      
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", 
                        "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin,");
//          response.header("Access-Control-Allow-Credentials", "true");
        });
        
//        get("/content/{id}", (request, response) -> {
//            // return image urls for a product id
//            response.header("Content-Type", "application/json");
//            return "{}";
//            
//        });
   
        get("/health", (request, response) -> {
            // health check status return
            response.status(200);
            return "OK";
        });

        get("/content/*/*", (request, response) -> {
            // return image
            // /content/<timestamp>/image_name
            // TODO: content type ?
            ContentS3DAO dao = new ContentS3DAO();
            try {
                response.status(200);
                byte[] content = dao.getProductFile("content/" + request.splat()[0] + "/" + request.splat()[1]);
                
                HttpServletResponse raw = response.raw();

                raw.getOutputStream().write(content);
                raw.getOutputStream().flush();
//                raw.getOutputStream().close();

                return response.raw();
            }catch (AmazonS3Exception e) {
                // no such key is the main reason..
                e.printStackTrace();
                response.status(404);
                return "{ \"error\": \"" + e.getMessage() + "\", \"status\": \"FAILED\"}" ;
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response.status(500);
                return "{ \"error\": \"" + e.getMessage() + "\", \"status\": \"FAILED\"}" ;
            }
//            response.header("Content-Type", "application/json");
//            return "{}";
            
        });
        
        options("/content/upload", (request, response) -> {
           System.out.println();
           // cors request solution..
           // this or remove the options header from the allowed method headers
           response.status(200);
           return "OK";
        });

        post("/content/upload", (request, response) -> {
            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

//            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
//                    location, maxFileSize, maxRequestSize, fileSizeThreshold);
            System.out.println(request);
            response.header("Content-Type", "application/json");
            // return path of the uploaded file
            // that path is directly callable from GET /content api
            Gson json = new Gson();
            Map<String, Object> responcedata = new HashMap<>();

            try {
                ContentS3Upload upload = new ContentS3Upload();
                String urlKey = upload.upload(request);

                responcedata.put("status", "SUCCESS");
                responcedata.put("productUrl", urlKey);
                response.status(200);
//              response.body(json.toJson(responcedata));
              System.out.println(json.toJson(responcedata));
//              return json.toJson(responcedata);
              
            } catch (IllegalStateException e) {
                // payload too large
                response.status(400);
                responcedata.put("status", "FAILED");
                responcedata.put("reason", e.getMessage().toString());
            
            } catch (Exception e) {
                response.status(500);
                responcedata.put("status", "FAILED");
                responcedata.put("reason", e.getMessage().toString());
                
            }
            return json.toJson(responcedata);
        });
        

        options("/content/*/*", (request, response) -> {
            // cors request solution for delete..
            // this or remove the options header from the allowed method headers
            response.status(200);
            return "OK";
         });

        delete("/content/*/*", (request, response) -> {
            // /content/<timestamp>/image_name
            ContentS3DAO dao = new ContentS3DAO();
            try {
                dao.deleteProduct("content/" + request.splat()[0] + "/" + request.splat()[1]);
            }catch (AmazonS3Exception e) {
                // no such key is the main reason..
                e.printStackTrace();
                response.status(404);
                return "{ \"error\": \"" + e.getMessage() + "\", \"status\": \"FAILED\"}" ;
                
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500);
                return "{ \"error\": \"" + e.getMessage() + "\", \"status\": \"FAILED\"}" ;
            }
            response.header("Content-Type", "application/json");
            response.status(202);
            return "{ \"status\": \"SUCCESS\"}" ;
            
        });

    }
    
}
