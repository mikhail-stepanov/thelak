package com.thelak.route.exceptions;

public class MsNotAuthorizedException extends MicroServiceException {

    public MsNotAuthorizedException() {
        super("Unauthorized");
    }

    @Override
    public String staticMessage(){
        return getMessage() == null || getMessage().isEmpty() ? "internal_error" : getMessage();
    }
}
