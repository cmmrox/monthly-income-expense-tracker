package com.cmm.mit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Application entry point for the Monthly Income/Expense Tracker backend.
 */
@SpringBootApplication
public class MitBackendApplication {
  /**
   * Main method used by Spring Boot to launch the application.
   */
  public static void main(String[] args) {
    run(args);
  }

  /**
   * Programmatic application entry point.
   *
   * <p>Exposed to make it easy to start/stop the application in tests or tooling.
   */
  static ConfigurableApplicationContext run(String[] args) {
    return SpringApplication.run(MitBackendApplication.class, args);
  }
}
