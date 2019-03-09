package com.github.miachm.sods;

/**
 * Describe an legal operation which it's not already supported
 */
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
