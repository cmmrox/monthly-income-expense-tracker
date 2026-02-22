package com.cmm.mit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 *
 * <p>Currently configures permissive CORS for local development.
 * Tighten this when deploying with auth.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configure CORS settings for API endpoints.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // When running behind the frontend Nginx reverse proxy (docker-compose), the browser origin
    // will be the frontend host (e.g. http://<vm-ip>:4200). Allow origin patterns for simplicity.
    // If you later add auth, tighten this list.
    registry.addMapping("/api/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false);
  }
}
