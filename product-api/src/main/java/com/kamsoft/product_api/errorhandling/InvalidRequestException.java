package com.kamsoft.product_api.errorhandling;

public class InvalidRequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message){
        super(message);
    }
}
