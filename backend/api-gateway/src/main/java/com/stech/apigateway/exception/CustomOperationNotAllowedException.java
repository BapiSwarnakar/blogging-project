package com.stech.apigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class CustomOperationNotAllowedException extends RuntimeException {

	private static final long serialVersionUID = -64481399407775860L;

	public CustomOperationNotAllowedException(String message) {
		super(message);
	}

	public CustomOperationNotAllowedException(String message, Throwable cause) {
		super(message, cause);
	}
}
