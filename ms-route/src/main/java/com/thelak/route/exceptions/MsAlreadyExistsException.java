package com.thelak.route.exceptions;

public class MsAlreadyExistsException extends MicroServiceException {

    public MsAlreadyExistsException() {
        super("Object already exists.");
    }

    @Override
    public String staticMessage(){
        return "object_already_exists";
    }
}
