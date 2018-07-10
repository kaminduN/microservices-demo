package com.kamsoft.api.dao;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import spark.Request;

public class ContentS3Upload {
    private final static String BUCKET = "products-media";
    private final static String B_PRODUCT_FOLDER = "content";
    private final static AmazonS3 client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    

    File uploadDir = new File("upload");
//    uploadDir.mkdir(); // create the upload directory if it doesn't exist
    
    public String upload(Request request) throws Exception {
//        Map<String, Object> requestMap = jsonToMap(request.body());
        int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB
//        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("./tmp/"));
        request.attribute("org.eclipse.jetty.multipartConfig",
                new MultipartConfigElement("./tmp", maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2));
        String S3_KEY_NAME = null;
        
        String id = UUID.randomUUID().toString();
        System.out.println(id);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String timestamp = sdf.format(date);
        System.out.println(timestamp);
        
        try {
            

            System.out.println(request.raw().getPart("filename"));
            Part file = request.raw().getPart("file");
            System.out.println(file.getContentType());
            System.out.println(file.getSize());
            System.out.println(file.getSubmittedFileName());

            InputStream fileInputStream = file.getInputStream();

            // MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            String extention = FilenameUtils.getExtension(file.getSubmittedFileName());
            System.out.println(extention);
            S3_KEY_NAME = String.join("/", B_PRODUCT_FOLDER, timestamp, id + "." + extention);
            System.out.println(S3_KEY_NAME);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, S3_KEY_NAME, fileInputStream, metadata);

            // actual upload to s3
             client.putObject(putObjectRequest);

        }catch (AmazonServiceException ase) {
             System.out.println("Caught an AmazonServiceException, which " +
                     "means your request made it " +
                     "to Amazon S3, but was rejected with an error response" +
                     " for some reason.");
             System.out.println("Error Message:    " + ase.getMessage());
             System.out.println("HTTP Status Code: " + ase.getStatusCode());
             System.out.println("AWS Error Code:   " + ase.getErrorCode());
             System.out.println("Error Type:       " + ase.getErrorType());
             System.out.println("Request ID:       " + ase.getRequestId());
             throw ase;
         } catch (AmazonClientException ace) {
             System.out.println("Caught an AmazonClientException, which " +
                     "means the client encountered " +
                     "an internal error while trying to " +
                     "communicate with S3, " +
                     "such as not being able to access the network.");
             System.out.println("Error Message: " + ace.getMessage());
             throw ace;
         
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
        // Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        
//
//        try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
//            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//        }finally {
//            tempFile.delete();
//        }
//        
        
        
        // using streams to avoid writing temp files..
        // fyi: streams handle files in memory
        
        
        
        
        return S3_KEY_NAME;
//        return "/content/products/someproduct.jpg";// return the dummy url
    }
    
    /*
    private void persistContent(Map<String, Object> requestMap) throws NoSuchAlgorithmException {
        List<Map<String, Object>> tripData = (List<Map<String, Object>>) requestMap.get("tripData");
        
        String tripName = requestMap.get("tripName").toString();
        for (Map<String, Object> data : tripData) {
            try {
                String fileName = data.get("fileName").toString();
                
                String key = ROOT + tripName + "/" + fileName;

                byte[] content = Base64.decodeBase64(data.get("content").toString());
                InputStream stream = new ByteArrayInputStream(content);

                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(content);
                byte[] digestBytes = messageDigest.digest();
                String streamMD5 = new String(Base64.encodeBase64(digestBytes));

                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentMD5(streamMD5);
                meta.setContentLength(content.length);

                client.putObject(new PutObjectRequest(BUCKET, key, stream, meta));
                
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                        + "to Amazon S3, but was rejected with an error response" + " for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
                        + "an internal error while trying to " + "communicate with S3, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            }
        }
    }
    */
}
