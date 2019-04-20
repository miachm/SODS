package com.github.miachm.sods;

/**
 * An unexpected error occurred when generate the ODS file
 */
public class GenerateOdsException extends SodsException{
    private final Exception cause;
    GenerateOdsException(Exception e)
    {
        cause = e;
        setStackTrace(e.getStackTrace());
        initCause(e);
    }

    @Override
    public String getMessage() {
        return cause.getMessage();
    }
}
