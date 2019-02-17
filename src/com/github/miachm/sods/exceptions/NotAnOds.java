package com.github.miachm.sods.exceptions;

public class NotAnOds extends RuntimeException {
    private String message;
    public NotAnOds(String s) {
        this.message = s;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
