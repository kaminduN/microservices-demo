package com.kamsoft.product_api.dao;

import javax.ws.rs.core.Request;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ProductS3Upload {
    private final static String BUCKET = "products-media";
    private final static String B_PRODUCT_FOLDER = "products/";
    private final static AmazonS3 client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    public void upload(Request request) {
        
    }

}

