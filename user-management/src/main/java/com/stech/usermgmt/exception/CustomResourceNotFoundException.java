package com.stech.usermgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -64481399407775860L;

	public CustomResourceNotFoundException(String message) {
		super(message);
	}

	public CustomResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
