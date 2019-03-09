package com.github.miachm.sods;

public class OperationNotSupportedException extends SodsException {
    private String message;
    OperationNotSupportedException(String s) {
        this.message = s;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
