package com.kamsoft.product_api.errorhandling;

import javax.ws.rs.core.Response.Status;

public class ProductMissingException extends AppException {

    /**
     * 
     */
    private static final long serialVersionUID = 8346520249026898770L;

    public ProductMissingException(int code, 
                                   String message,
                                   String developerMessage) {
        super(Status.NOT_FOUND.getStatusCode(), code, message, developerMessage);
    }
    
    public ProductMissingException(String message) {
        super(Status.NOT_FOUND.getStatusCode(), 2, message, null);
    }
    
    public ProductMissingException(String message, String developerMessage) {
        super(Status.NOT_FOUND.getStatusCode(), 2, message, developerMessage);
    }
}
