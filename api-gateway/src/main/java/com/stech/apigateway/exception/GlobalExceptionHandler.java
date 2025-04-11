package com.stech.apigateway.exception;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Generic handler for miscellaneous exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception e) {
        return buildErrorResponse(e, null, HttpStatus.INTERNAL_SERVER_ERROR, "Unhandled Exception", Collections.singletonList("An unexpected error occurred."));
    }
    
    // Add another exception ...
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, ZonedDateTime timestamp, HttpStatus status, String error, Object errors) {
    	ErrorResponse errorResponse = ErrorResponse.builder()
    		.timestamp(ZonedDateTime.now())	
	        .status(status)
	        .message(e.getMessage()) // Can include the e.getClass().getSimpleName()
	        .errors(errors)
	        .build();
    
    	return ResponseEntity.status(status).body(errorResponse); // Return appropriate HTTP status
    		
    	//return ResponseEntity.status(HttpStatus.OK).body(errorResponse);  // Always returning 200 OK
	}
}
