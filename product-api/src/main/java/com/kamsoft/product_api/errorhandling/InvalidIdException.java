package com.kamsoft.product_api.errorhandling;

public class InvalidIdException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5630410181024249296L;

    public InvalidIdException(String message) {
        super(message);
    }
  }