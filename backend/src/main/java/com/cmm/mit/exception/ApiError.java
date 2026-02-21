package com.cmm.mit.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(Error error, Meta meta) {
  public static ApiError of(String code, String message, List<FieldError> details) {
    return new ApiError(new Error(code, message, details), new Meta(null, Instant.now()));
  }

  public record Error(String code, String message, List<FieldError> details) {}
  public record FieldError(String field, String message) {}
  public record Meta(String requestId, Instant ts) {}
}
