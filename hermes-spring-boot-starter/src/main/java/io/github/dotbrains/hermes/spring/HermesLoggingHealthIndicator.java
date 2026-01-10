package io.github.dotbrains.hermes.spring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Health indicator for Hermes logging.
 */
public class HermesLoggingHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
            .withDetail("provider", "Hermes")
            .withDetail("status", "operational")
            .build();
    }
}
