package com.cmm.mit;

import org.junit.jupiter.api.Test;

class MitBackendApplicationTest {

  @Test
  void run_startsAndStopsApplicationContext() {
    try (var ctx = MitBackendApplication.run(new String[] {"--spring.main.web-application-type=none", "--spring.main.banner-mode=off"})) {
      // no-op
    }
  }
}
