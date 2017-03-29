package com.github.miachm.SODS;

/**
 * Created by MiguelPC on 29/03/2017.
 */
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
