package com.grocery.exception;

/**
 * Exception thrown for catalog lookup or catalog-related errors.
 */
public class CatalogException extends GroceryException {
    public CatalogException(String message) {
        super(message);
    }

    public CatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
