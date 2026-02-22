package com.cmm.mit.util;

/**
 * Minimal helper for safe logging.
 *
 * Keep it conservative: truncate long strings and avoid dumping huge payloads.
 * This project currently has no auth/secrets in requests, but we still avoid
 * unbounded log growth.
 */
public final class LogSanitizer {

  private LogSanitizer() {}

  public static String safe(Object value) {
    if (value == null) return null;
    String text = String.valueOf(value);
    return truncate(text, 500);
  }

  public static String truncate(String text, int maxLen) {
    if (text == null) return null;
    if (text.length() <= maxLen) return text;
    return text.substring(0, maxLen) + "…";
  }
}
