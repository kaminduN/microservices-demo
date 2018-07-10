package com.kamsoft.product_api.dao;

import java.util.List;

import com.kamsoft.product_api.errorhandling.AppException;
import com.kamsoft.product_api.errorhandling.ProductMissingException;
import com.kamsoft.product_api.model.ProductDTO;

public interface ProductDAO {
    public List<ProductDTO> listProducts() throws AppException;  
    public ProductDTO getProductById(String productId) throws ProductMissingException;
    public ProductDTO createProduct(ProductDTO product) throws AppException;
    public String updateProduct(ProductDTO product) throws AppException;
    public String deleteProductById(String productId) throws AppException;
}
