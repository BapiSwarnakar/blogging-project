package com.stech.authentication.exception;

public class CustomAuthException extends RuntimeException {
    public CustomAuthException(String message) { super(message); }
    public CustomAuthException(String message, Throwable cause) { super(message, cause); }
}
