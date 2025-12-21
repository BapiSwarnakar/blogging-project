package com.stech.apigateway.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class CustomJwtTokenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5097002967851944219L;

	public CustomJwtTokenException(String message) {
        super(message);
    }
	
    public CustomJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
