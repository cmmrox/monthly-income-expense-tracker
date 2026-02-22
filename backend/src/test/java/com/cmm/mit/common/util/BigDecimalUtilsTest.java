package com.cmm.mit.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BigDecimalUtilsTest {

  @Test
  void zeroIfNull_whenNull_returnsZero() {
    assertThat(BigDecimalUtils.zeroIfNull(null)).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  void zeroIfNull_whenNonNull_returnsSameInstanceValue() {
    BigDecimal value = new BigDecimal("12.34");
    assertThat(BigDecimalUtils.zeroIfNull(value)).isSameAs(value);
  }
}
