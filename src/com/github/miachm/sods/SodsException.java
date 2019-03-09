package com.github.miachm.sods;

/**
 * Base class for all exceptions of the library
 */
public abstract class SodsException extends RuntimeException {
    abstract public String getMessage();
}
