package com.stech.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomResourceAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = -64481399407775860L;

	public CustomResourceAlreadyExistsException(String message) {
		super(message);
	}

	public CustomResourceAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
