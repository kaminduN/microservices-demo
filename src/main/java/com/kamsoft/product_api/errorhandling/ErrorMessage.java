package com.kamsoft.product_api.errorhandling;

import java.io.Serializable;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

public class ErrorMessage implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4662831912219663026L;

    int status;
    /** application specific error code */
    int code; 

    /** Message describing the error */
    String message;
    
    /** detailed error description for developers*/
    String developerMessage; 

    public ErrorMessage() {}
    
    public ErrorMessage(AppException exp) {
        super();
        this.status = exp.getStatus();
        this.code =  exp.getStatus();
        this.message = exp.getMessage();
        this.developerMessage = exp.getDeveloperMessage();
    }

    public ErrorMessage(NotFoundException ex){
        this.status = Response.Status.NOT_FOUND.getStatusCode();
        this.message = ex.getMessage();
    }
    
    public ErrorMessage(InvalidRequestException e) {
        this.status = Response.Status.BAD_REQUEST.getStatusCode();
        this.message = "This request was no good.";
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
