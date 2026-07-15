package com.ejada.practice.dayoneandtwo.exception;

/**
 * Thrown when an operation would violate a uniqueness constraint (e.g.
 * creating an employee with an email that's already in use).
 * Translated to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
