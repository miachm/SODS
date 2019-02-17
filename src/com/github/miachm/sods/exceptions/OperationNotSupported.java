package com.github.miachm.sods.exceptions;

public class OperationNotSupported extends RuntimeException {
    private String message;
    public OperationNotSupported(String s) {
        this.message = s;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
