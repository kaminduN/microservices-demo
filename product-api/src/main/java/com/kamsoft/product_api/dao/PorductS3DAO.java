package com.kamsoft.product_api.dao;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class PorductS3DAO {
    private final static String BUCKET = "products-media";
    private final static String B_PRODUCT_FOLDER = "products/";
    private final static AmazonS3 client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    
    public List<String> listFiles(String productId) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(BUCKET)
                .withPrefix(B_PRODUCT_FOLDER + productId);

        ObjectListing objects = client.listObjects(listObjectsRequest);
        
        List<String> files = new ArrayList<>();
        for (S3ObjectSummary summary: objects.getObjectSummaries()) {
            System.out.println(summary.getKey());
            files.add(summary.getKey());
        }
        
        return files;
    }
    
    public List<String> listProductsIds() {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(BUCKET)
                .withPrefix(B_PRODUCT_FOLDER)
                .withDelimiter("/");
        ObjectListing objects = client.listObjects(listObjectsRequest);
        
        return objects.getCommonPrefixes();
    }
    
    
}
