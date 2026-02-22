package com.cmm.mit.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Test
  void of_buildsErrorWithMeta() {
    ApiError apiError = ApiError.of("CODE", "Message", List.of(new ApiError.FieldError("f", "m")));

    assertThat(apiError.error().code()).isEqualTo("CODE");
    assertThat(apiError.error().details()).hasSize(1);
    assertThat(apiError.meta().ts()).isNotNull();
  }
}
