package com.kamsoft.product_api.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kamsoft.product_api.dao.ProductDAO;
import com.kamsoft.product_api.dao.ProductDynamoDAO;
import com.kamsoft.product_api.errorhandling.AppException;
import com.kamsoft.product_api.errorhandling.ProductMissingException;
import com.kamsoft.product_api.model.ProductDTO;


@Path("/products")
public class ProductsResource {

    private final ProductDAO dao;
    private static final Logger LOG = LoggerFactory.getLogger(ProductsResource.class);

    public ProductsResource() {
        dao = new ProductDynamoDAO();
    } 
    
    //FIXME: cors enable to jercey jetty...
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response returnALL() throws AppException{
          LOG.debug("GET /products");
         
//        List<Product> products = new ArrayList<>();
//        products.add(new Product("Smith", "cool product", 10.50));
//        products.add(new Product("Jane", "something", 12.5));
        List<ProductDTO> products = dao.listProducts();
        
        Map<String, Object> productData = new HashMap<>();
        productData.put("tripData", products);

        return Response.status(200).entity(productData).build();
//        .header("Access-Control-Allow-Origin", "*")
//        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
//        .allow("OPTIONS").build();
    }
    
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductById(@PathParam("id") String productId) throws ProductMissingException {
        LOG.debug("GET /products/{"+ productId+ "}");
        ProductDTO product = dao.getProductById(productId);
        System.out.println(product);
//		return new ProductDTO("Smith", "cool product", 10.50);
//        return product;
        return Response.ok(product).build();
	}
	
	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(ProductDTO data) throws AppException {
        LOG.debug("POST /products");
        String body = "Data post: "+data;
        LOG.info(body);
        LOG.info(data.getName());
        ProductDTO createdProduct = dao.createProduct(data);
        LOG.info("Product created successfully");
        // upload images and attach the links ?
        return Response.status(201).entity(createdProduct).build();

    }

    //update operation
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putProductDatabyId(@PathParam("id") String name, ProductDTO data) throws AppException {
        LOG.debug("PUT /products/{"+ name+ "}");
        data.setProductIndex(name);//override the auto generated id with the one given
        dao.updateProduct(data);
        return Response.status(200).entity(name + " updated").build();
    }


    @DELETE
    @Path("/{id}")
    public Response deleteProductData(@PathParam("id") String productId) throws AppException {
        LOG.debug("DELETE /products/{"+ productId+ "}");
        String status = dao.deleteProductById(productId);
        if (status == null) {
            System.out.println("item not deleted....");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(204).entity(productId + " deleted").build();
    }
	
}
