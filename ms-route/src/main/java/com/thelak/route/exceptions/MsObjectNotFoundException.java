package com.thelak.route.exceptions;

public class MsObjectNotFoundException extends MicroServiceException {

    public MsObjectNotFoundException(String objectType, String objectId) {
        super(String.format("Object Not found. Type: %s. Search parameters: %s", objectType, objectId));
    }

    @Override
    public String staticMessage(){
        return "object_not_found_error";
    }
}
