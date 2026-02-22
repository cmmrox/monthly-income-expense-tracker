package com.cmm.mit.common.util;

import java.math.BigDecimal;

/**
 * Utility methods for working with {@link BigDecimal} values.
 *
 * <p>This class is intentionally stateless and should only contain pure helpers.
 */
public final class BigDecimalUtils {

  private BigDecimalUtils() {
    // utility class
  }

  /**
   * Returns {@link BigDecimal#ZERO} when the provided value is {@code null}.
   *
   * @param value value to normalize
   * @return {@link BigDecimal#ZERO} if {@code value} is {@code null}; otherwise the same value
   */
  public static BigDecimal zeroIfNull(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
