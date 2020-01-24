package com.challenge.exception;

public class StarredNotFoundException extends RuntimeException {

    public StarredNotFoundException(Long id) {
        super("Starred repo id " + id + " not found");
    }

}
