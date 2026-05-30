package com.github.miachm.sods;

/**
 * Thrown when opening an encrypted ODS document with a missing or incorrect password.
 */
public class WrongPasswordException extends SodsException {
    private final String message;

    WrongPasswordException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
