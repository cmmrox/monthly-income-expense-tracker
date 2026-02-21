package com.cmm.mit.dto;

import java.time.Instant;

public record ApiEnvelope<T>(T data, Meta meta) {
  public static <T> ApiEnvelope<T> ok(T data) {
    return new ApiEnvelope<>(data, new Meta(null, Instant.now()));
  }

  public record Meta(String requestId, Instant ts) {}
}
