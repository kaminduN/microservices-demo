package com.kamsoft.product_api.errorhandling;

public class AppException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 4807234003005598002L;

    
    /** 
     * contains redundantly the HTTP status of the response sent back to the client in case of error, so that
     * the developer does not have to look into the response headers. If null a default 
     */
    Integer status;
    
    /** application specific error code */
    int code; 
    
    
    String message;
    /** detailed error description for developers*/
    String developerMessage;    

    public AppException(int status, 
                        int code, 
                        String message,
                        String developerMessage) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
        this.developerMessage = developerMessage;
    }

    public AppException() { }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

}
