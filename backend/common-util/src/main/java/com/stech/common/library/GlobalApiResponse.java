package com.stech.common.library;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalApiResponse {

	public static <T> ApiResult<T> success(T data, String message) {
		return new ApiResult<>(ZonedDateTime.now(), "SUCCESS", data, message, Collections.emptyList(), null);
	}

	public static <T> ApiResult<T> success(T data, String message, PageInfo pageInfo) {
		return new ApiResult<>(ZonedDateTime.now(), "SUCCESS", data, message, Collections.emptyList(), pageInfo);
	}

	public static ApiResult<Object> error(String message, String error) {
		return new ApiResult<>(ZonedDateTime.now(), "ERROR", null, message, Collections.singletonList(error), null);
	}

	public static ApiResult<Object> error(String message, List<String> errors) {
		return new ApiResult<>(ZonedDateTime.now(), "ERROR", null, message, errors, null);
	}

	@Getter
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ApiResult<T> {
		private final ZonedDateTime timestamp;
		private final String status;
		private final T data;
		private final String message;
		private final List<String> errors;
		private final PageInfo pageInfo;
	}

	@Getter
	@AllArgsConstructor
	public static class PageInfo {
		private final int size;
		private final int number;
		private final long totalElements;
		private final int totalPages;
	}
}