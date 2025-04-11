package com.stech.apigateway.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalRuntimeExceptionResponse extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4391985404505272595L;
	
	public Boolean status;
	public Integer code;
	public Object message;

}
