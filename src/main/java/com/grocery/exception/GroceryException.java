package com.grocery.exception;

/**
 * Base runtime exception for the Grocery Store application.
 * Use for application-level errors that are not recoverable at the current call site.
 */
public class GroceryException extends RuntimeException {
    public GroceryException(String message) {
        super(message);
    }

    public GroceryException(String message, Throwable cause) {
        super(message, cause);
    }
}
