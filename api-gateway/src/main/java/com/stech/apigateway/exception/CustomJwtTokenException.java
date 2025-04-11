package com.stech.apigateway.exception;

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
