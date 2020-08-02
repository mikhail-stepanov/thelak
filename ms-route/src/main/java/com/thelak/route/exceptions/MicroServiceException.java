package com.thelak.route.exceptions;

public class MicroServiceException extends Exception {

    private final String details;

    public MicroServiceException(String message){
        super(message);
        details = null;
    }

    public MicroServiceException(String message, String details){
        super(message);
        this.details = details;
    }

    public String staticMessage(){
        return "error";
    }
}
