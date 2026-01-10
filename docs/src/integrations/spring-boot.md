# Spring Boot Integration

Hermes provides native Spring Boot integration through auto-configuration and YAML-based configuration.

## Installation

Add the Spring Boot starter dependency:

### Maven

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
dependencies {
    implementation 'io.github.dotbrains:hermes-spring-boot-starter:1.0.0-SNAPSHOT'
}
```

## Configuration

Configure Hermes in `application.yml`:

```yaml
hermes:
  # Root and package-specific log levels
  level:
    root: INFO
    packages:
      io.github.dotbrains: DEBUG
      com.example: TRACE

  # Log pattern for console output
  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

  # Async logging with LMAX Disruptor
  async:
    enabled: true
    queue-size: 1024
    block-when-full: false
```

## Auto-Configuration

Hermes auto-configures when `hermes-spring-boot-starter` is on the classpath.

### What Gets Auto-Configured

1. **Logger Provider**: Registers `HermesLoggerProvider` via ServiceLoader
2. **Log Levels**: Configures root and package-specific log levels
3. **Appenders**: Sets up console and file appenders with configured patterns
4. **Async Logging**: Wraps appenders with AsyncAppender when enabled
5. **Health Indicator**: Exposes logging status to Spring Boot Actuator

### Configuration Properties

All properties are bound under the `hermes` prefix:

```java
@ConfigurationProperties(prefix = "hermes")
public class HermesProperties {
    private LevelConfig level = new LevelConfig();
    private String pattern;
    private AsyncConfig async = new AsyncConfig();
    // ...
}
```

## Usage in Spring Beans

### With @InjectLogger

```java
import io.github.dotbrains.InjectLogger;
import org.springframework.stereotype.Service;

@Service
@InjectLogger
public class UserService extends UserServiceHermesLogger {

    public void createUser(String username) {
        log.info("Creating user: {}", username);
    }
}
```

### Manual Logger Creation

```java
import io.github.dotbrains.Logger;
import io.github.dotbrains.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void processOrder(Long orderId) {
        log.info("Processing order: {}", orderId);
    }
}
```

## MDC with Web Requests

Configure MDC automatically for all requests:

```java
import io.github.dotbrains.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Set MDC values for this request
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("method", request.getMethod());
            MDC.put("path", request.getRequestURI());
            MDC.put("clientIp", request.getRemoteAddr());

            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC after request
            MDC.clear();
        }
    }
}
```

## Custom Appender Configuration

Configure appenders programmatically:

```java
import io.github.dotbrains.core.appender.*;
import io.github.dotbrains.core.layout.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public ConsoleAppender consoleAppender(HermesProperties properties) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setLayout(new PatternLayout(properties.getPattern()));
        appender.start();
        return appender;
    }

    @Bean
    public FileAppender fileAppender() {
        FileAppender appender = new FileAppender("logs/application.log");
        appender.setLayout(new PatternLayout(
            "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %logger - %msg%n"
        ));
        appender.start();
        return appender;
    }

    @Bean
    public AsyncAppender asyncAppender(FileAppender fileAppender,
                                       HermesProperties properties) {
        AsyncAppender async = new AsyncAppender(fileAppender);
        async.setQueueSize(properties.getAsync().getQueueSize());
        async.setBlockWhenFull(properties.getAsync().isBlockWhenFull());
        async.start();
        return async;
    }
}
```

## Profiles-Based Configuration

Different configurations for different environments:

### application.yml (default)

```yaml
hermes:
  level:
    root: INFO
  pattern: "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
```

### application-dev.yml

```yaml
hermes:
  level:
    root: DEBUG
    packages:
      com.example: TRACE
  pattern: "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
  async:
    enabled: false  # Synchronous for easier debugging
```

### application-prod.yml

```yaml
hermes:
  level:
    root: WARN
    packages:
      com.example: INFO
  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId}] %logger - %msg%n"
  async:
    enabled: true
    queue-size: 8192
    block-when-full: false
```

## Actuator Integration

### Health Indicator

Hermes provides a health indicator for Spring Boot Actuator:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always
```

Access health status at: `http://localhost:8080/actuator/health`

Response:
```json
{
  "status": "UP",
  "components": {
    "hermesLogging": {
      "status": "UP",
      "details": {
        "appenders": 2,
        "asyncEnabled": true,
        "queueSize": 1024
      }
    }
  }
}
```

### Custom Health Indicator

```java
import io.github.dotbrains.core.appender.AsyncAppender;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("hermesLogging")
public class HermesHealthIndicator implements HealthIndicator {

    private final AsyncAppender asyncAppender;

    public HermesHealthIndicator(AsyncAppender asyncAppender) {
        this.asyncAppender = asyncAppender;
    }

    @Override
    public Health health() {
        long droppedEvents = asyncAppender.getDroppedEventCount();

        if (droppedEvents > 1000) {
            return Health.down()
                .withDetail("droppedEvents", droppedEvents)
                .withDetail("message", "Too many dropped log events")
                .build();
        }

        return Health.up()
            .withDetail("droppedEvents", droppedEvents)
            .withDetail("queueSize", asyncAppender.getQueueSize())
            .build();
    }
}
```

## Logstash Integration

Configure Logstash appender for ELK stack:

```yaml
hermes:
  logstash:
    enabled: true
    host: logstash.example.com
    port: 5000
    application-name: ${spring.application.name}
    environment: ${spring.profiles.active}
    connection-timeout: 5000
    write-timeout: 10000
```

Bean configuration:

```java
@Configuration
@ConditionalOnProperty("hermes.logstash.enabled")
public class LogstashConfig {

    @Bean
    public LogstashAppender logstashAppender(HermesProperties properties) {
        LogstashProperties logstash = properties.getLogstash();

        LogstashAppender appender = new LogstashAppender(
            logstash.getHost(),
            logstash.getPort()
        );

        appender.setApplicationName(logstash.getApplicationName());
        appender.setEnvironment(logstash.getEnvironment());
        appender.setConnectionTimeout(logstash.getConnectionTimeout());
        appender.setWriteTimeout(logstash.getWriteTimeout());

        AsyncAppender async = new AsyncAppender(appender);
        async.setQueueSize(8192);
        async.start();

        return async;
    }
}
```

## Testing

### Test Configuration

Reduce log verbosity in tests:

```yaml
# src/test/resources/application-test.yml
hermes:
  level:
    root: WARN
    packages:
      com.example: INFO
  async:
    enabled: false  # Synchronous for predictable test output
```

### Test Logging

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        // Logs will use test configuration
        userService.createUser("testuser");
    }
}
```

## Best Practices

1. **Use profiles** - Different log levels for dev/test/prod
2. **Enable async in production** - Better performance
3. **Include request IDs** - Use MDC filter for traceability
4. **Monitor health** - Use Actuator health indicators
5. **Structured logging** - Use JSON layout for production
6. **Configure retention** - Use RollingFileAppender with max history
7. **Test configuration** - Verify log output in integration tests

## Complete Example

```yaml
# application-prod.yml
spring:
  application:
    name: order-service

hermes:
  level:
    root: INFO
    packages:
      com.example: INFO
      org.springframework: WARN

  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId}] [%X{userId}] %logger{36} - %msg%n"

  async:
    enabled: true
    queue-size: 8192
    block-when-full: false

  logstash:
    enabled: true
    host: logstash.internal.example.com
    port: 5000
    application-name: ${spring.application.name}
    environment: production
    connection-timeout: 5000
    write-timeout: 10000

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

With this configuration:

- Logs to console with structured pattern including MDC
- Sends logs to Logstash for centralized logging
- Uses async logging for high performance
- Exposes health status via Actuator
- Ready for production use
