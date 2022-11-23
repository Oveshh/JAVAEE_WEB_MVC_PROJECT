package com.springmvc.exceptions;

public class ContextException extends  RuntimeException{
    public ContextException(String message) {
        super(message);
    }

    public ContextException(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
