package com.stech.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class CustomNotAcceptableException extends RuntimeException {
    public CustomNotAcceptableException(String message) { super(message); }
    public CustomNotAcceptableException(String message, Throwable cause) { super(message, cause); }
}
