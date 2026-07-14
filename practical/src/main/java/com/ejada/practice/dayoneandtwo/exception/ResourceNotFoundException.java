package com.ejada.practice.dayoneandtwo.exception;

/**
 * Thrown when a requested resource (e.g. an employee id) does not exist.
 * Translated to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
