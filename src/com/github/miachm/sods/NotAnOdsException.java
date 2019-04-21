package com.github.miachm.sods;

/**
 * The file provided is not an ODS file
 */
public class NotAnOdsException extends SodsException {
    private final Exception cause;
    private String message;
    NotAnOdsException(String string) {
        cause = null;
        message = string;
    }
    NotAnOdsException(Exception e) {
        cause = e;
        setStackTrace(e.getStackTrace());
        initCause(e);
        message = cause.getMessage();
    }

    @Override
    public String getMessage(){
        return message;
    }
}
