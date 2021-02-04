package com.thelak.route.exceptions;

public class MsAlreadyExistsException extends MicroServiceException {

    public MsAlreadyExistsException(String ex) {
        super(ex);
    }

    @Override
    public String staticMessage(){
        return "object_already_exists";
    }
}
