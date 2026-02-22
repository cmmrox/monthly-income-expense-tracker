package com.cmm.mit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point for the Monthly Income/Expense Tracker backend.
 */
@SpringBootApplication
public class MitBackendApplication {
  /**
   * Main method used by Spring Boot to launch the application.
   */
  public static void main(String[] args) {
    SpringApplication.run(MitBackendApplication.class, args);
  }
}
