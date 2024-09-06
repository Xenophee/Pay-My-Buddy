package com.paymybuddy.exception;

public class UnexpectedNotFoundException extends RuntimeException {
    public UnexpectedNotFoundException(String message) {
        super(message);
    }
}
