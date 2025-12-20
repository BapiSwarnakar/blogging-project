package com.stech.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class CustomBadRequestException extends RuntimeException {

	private static final long serialVersionUID = 3048774177336990790L;

	public CustomBadRequestException(String message) {
		super(message);
	}

	public CustomBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
