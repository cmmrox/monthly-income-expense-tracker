package com.cmm.mit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogSanitizerTest {

  @Test
  void safe_whenNull_returnsNull() {
    assertThat(LogSanitizer.safe(null)).isNull();
  }

  @Test
  void truncate_whenShort_returnsSame() {
    assertThat(LogSanitizer.truncate("abc", 5)).isEqualTo("abc");
  }

  @Test
  void truncate_whenLong_appendsEllipsis() {
    String s = "abcdefghij";
    assertThat(LogSanitizer.truncate(s, 5)).isEqualTo("abcde…");
  }
}
