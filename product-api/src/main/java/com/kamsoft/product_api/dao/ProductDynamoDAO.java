/**
 * 
 */
package com.kamsoft.product_api.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import com.kamsoft.product_api.errorhandling.AppException;
import com.kamsoft.product_api.errorhandling.ProductMissingException;
import com.kamsoft.product_api.model.Product;
import com.kamsoft.product_api.model.ProductDTO;

/**
 * Stores products in the AWS Dynamo DB 
 *
 */
public class ProductDynamoDAO implements ProductDAO {

    private final static String DBTable = "ProductCatalog";
    private final static int PAGE_LIMIT = 20;
    private final static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_2).build();

    static DynamoDB dynamoDB = new DynamoDB(client);
    private static final Logger LOG = LoggerFactory.getLogger(ProductDynamoDAO.class);
    
    public List<ProductDTO> listProducts() throws AppException {
        return listProducts(null);
    }

    public List<ProductDTO> listProducts(String nextKey) throws AppException {
        // Don't return all items per-say
        // Get the first page of items
        //Map<String, Object> products = new HashMap<String, Object>();
        // could use a query to increase performance ?
        List<ProductDTO> products = new ArrayList<ProductDTO>();
        Map<String, AttributeValue> lastKeyEvaluated = null;
        
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DBTable)
                .withLimit(PAGE_LIMIT)
                .withExclusiveStartKey(lastKeyEvaluated);

        try {
            ScanResult result = client.scan(scanRequest);
            System.out.println("Printing item after retrieving it....");
            for (Map<String, AttributeValue> item : result.getItems()){
                //Print and check
                System.out.println(String.join(" | ", 
                            item.get(Product.PRODUCT_ID).getS(),
                            item.get(Product.NAME).getS(),
                            item.get(Product.DESCRIPTION).getS(),
                            item.get(Product.PRICE).getN()));

                products.add(new ProductDTO(item.get(Product.PRODUCT_ID).getS(),
                                            item.get(Product.NAME).getS(),
                                            item.get(Product.DESCRIPTION).getS(),
                                            item.get(Product.PRODUCT_URL).getS(),
                                            Double.parseDouble(item.get(Product.PRICE).getN())));            
            }
            //TODO: return a map instead of a list
            // in that way we can easily set properties as needed
            LOG.debug("Dynamodb returned products : "+ products.size());
        } catch (ResourceNotFoundException e) {
            LOG.error("Error in retriving product items !!: table does not exists");
            LOG.error(e.toString());
            throw new AppException(500, 1, "internal server error", e.toString());
        }

        return products;
    }

    public ProductDTO getProductById(String productId) throws ProductMissingException {
        // TODO Auto-generated method stub
        Table table = dynamoDB.getTable(DBTable);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(Product.PRODUCT_ID + " = :v_id")
                .withValueMap(new ValueMap()
                    .withString(":v_id", productId));

        ItemCollection<QueryOutcome> items = table.query(spec);
        System.out.println("Results returned :"+ items.getMaxResultSize()); 
        Iterator<Item> iterator = items.iterator();
        Item item = null;

        // only one item matching primary key
        if (iterator.hasNext()) {
            item = iterator.next();
            System.out.println(item.toJSONPretty());
        }else {
            System.out.println("Item missing...!!");
            throw new ProductMissingException("product "+ productId + " does not exists");
        }

        return new ProductDTO(item.getString(Product.PRODUCT_ID),
                              item.getString(Product.NAME),
                              item.getString(Product.DESCRIPTION),
                              item.getString(Product.PRODUCT_URL),
                              item.getDouble(Product.PRICE));
    }

    public ProductDTO createProduct(ProductDTO product) throws AppException {

        Table table = dynamoDB.getTable(DBTable);
//        String productIndex = UUID.randomUUID().toString(); // auto gen id

        try {
            System.out.println("Processing record #" + product.getProductIndex());
            // FIXME: is the product is partially updated the rest of the keys
            // become null
            Item item = new Item()
                .withPrimaryKey(Product.PRODUCT_ID, product.getProductIndex())
                .withString(Product.NAME, product.getName())
                .withNumber(Product.PRICE, product.getPrice())
                .withBoolean(Product.ISACTIVE, true)
                .withString(Product.DESCRIPTION, product.getDescription())
                .withString(Product.PRODUCT_URL, product.getProductimage());
            table.putItem(item);
            LOG.debug("product successfully processed");

        }
        catch (Exception e) {
            System.err.println("Failed to create item " + product.getProductIndex() + " in " + DBTable);
            System.err.println(e.getMessage());
            throw new AppException(500, 1, "internal server error", e.getMessage());
        }

        return product;
    }

    public String updateProduct(ProductDTO product) throws ProductMissingException {
        Table table = dynamoDB.getTable(DBTable);
        String productIndex = product.getProductIndex();

        System.out.println("Processing record #" + productIndex);
        
        // check for the existing of a product and throw error if not
        ProductDTO existingproduct = getProductById(productIndex);
        
        if (product.getName() == null) {
            product.setName(existingproduct.getName());
        }
        if (product.getDescription() == null) {
            product.setDescription(existingproduct.getDescription());
        }
        // default value of double check and update..
        if (product.getPrice() == Double.NaN) {
            product.setPrice(existingproduct.getPrice());
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(Product.PRODUCT_ID, productIndex)
                .withReturnValues(ReturnValue.ALL_NEW)
                .withUpdateExpression("SET #n = :val1, #p = :val2, #d = :val3")
                .withNameMap(new NameMap()
                            .with("#n", Product.NAME)
                            .with("#p", Product.PRICE)
                            .with("#d", Product.DESCRIPTION))
                .withValueMap(new ValueMap()
                            .withString(":val1", product.getName())
                            .withNumber(":val2", product.getPrice())
                            .withString(":val3", product.getDescription()));

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
//            / Check the response.
            System.out.println("Printing item after conditional update to new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());

        }catch (ConditionalCheckFailedException e) {
            LOG.error("Cannot update item");
            LOG.error("Error updating item"+ productIndex +" in " + DBTable);
            LOG.error(e.getMessage());
            throw e;// throw the exact message to out since unexpected
        }    
            
        return productIndex;
    }

    public String deleteProductById(String productId) throws AppException {
        Table table = dynamoDB.getTable(DBTable);

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(Product.PRODUCT_ID, productId)
                .withConditionExpression("#id = :val")
                .withNameMap(new NameMap().with("#id", Product.PRODUCT_ID))
                .withValueMap(new ValueMap()
                        .withString(":val", productId))
                .withReturnValues(ReturnValue.ALL_OLD);
        try {
            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
            // Check the response.
            System.out.println("Printing item that was deleted...");
            System.out.println(outcome.getItem().toJSONPretty());

        }catch (ConditionalCheckFailedException e) {
            //item not exist
            System.out.println(e.toString());
            throw new ProductMissingException("Product does not exist", e.toString());
        }
        return productId;
    }
}
