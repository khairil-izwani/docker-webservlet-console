package com.khairil.host;

/**
 * All validation exceptions should be wrapped here.
 * 
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 4746606040712701991L;

    public ValidationException(String message) {
        super(message);
    }

}
