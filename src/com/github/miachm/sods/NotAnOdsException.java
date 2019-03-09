package com.github.miachm.sods;

/**
 * The file provided is not an ODS file
 */
public class NotAnOdsException extends SodsException {
    private String message;
    NotAnOdsException(String s) {
        this.message = s;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
