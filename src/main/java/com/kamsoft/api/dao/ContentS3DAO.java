package com.kamsoft.api.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

//public class ContentS3DAO implements ContentDAO{
public class ContentS3DAO{

    private final static String BUCKET = "products-media";
    private final static String B_PRODUCT_FOLDER = "content";
    private final static AmazonS3 client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    public String test() {
        return "{'message': 'Hello world'}";
    }
    
    public byte[] getProductFile(String key) throws Exception {
//        Map<String, Object> data = new HashMap<>();
        System.out.println(key);
        S3Object object = client.getObject(new GetObjectRequest(BUCKET, key));
        
        System.out.println("Content-Type: "  + 
                object.getObjectMetadata().getContentType());
        try {
        InputStream objectData = object.getObjectContent();
        byte[] content = IOUtils.toByteArray(objectData);
        // process the file....
        
//        FileOutputStream fos = new FileOutputStream(new File(key));
//        byte[] read_buf = new byte[1024];
//        int read_len = 0;
//        while ((read_len = objectData.read(read_buf)) > 0) {
//            fos.write(read_buf, 0, read_len);
//        }
        objectData.close();
        return content;
//        fos.close();
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            throw ase;
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            throw ace;
//        } catch (FileNotFoundException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            System.exit(1);
            throw e;
        }
        
//        return null;
    }
    
//    public List<String> listFiles(String productId) {
//        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
//                .withBucketName(BUCKET)
//                .withPrefix(B_PRODUCT_FOLDER + productId);
//
//        ObjectListing objects = client.listObjects(listObjectsRequest);
//        
//        List<String> files = new ArrayList<>();
//        for (S3ObjectSummary summary: objects.getObjectSummaries()) {
//            System.out.println(summary.getKey());
//            files.add(summary.getKey());
//        }
//        
//        return files;
//    }
//    
//    public List<String> listProductsIds() {
//        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
//                .withBucketName(BUCKET)
//                .withPrefix(B_PRODUCT_FOLDER)
//                .withDelimiter("/");
//        ObjectListing objects = client.listObjects(listObjectsRequest);
//        
//        return objects.getCommonPrefixes();
//    }
    
    
}
