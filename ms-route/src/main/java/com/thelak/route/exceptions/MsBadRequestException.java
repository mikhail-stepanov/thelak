package com.thelak.route.exceptions;

public class MsBadRequestException extends MicroServiceException {

    public MsBadRequestException(String parametername, String parameterValue) {
        super(String.format("Incorrect argument. Name: %s. Value: %s", parametername, parameterValue));
    }

    public MsBadRequestException(String parametername) {
        super(String.format("Incorrect argument. Name: %s. Value: null", parametername));
    }

    @Override
    public String staticMessage(){
        return "bad_request_error";
    }
}
