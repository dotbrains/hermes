# Quick Start

Get up and running with Hermes in just a few minutes.

## Installation

=== "Maven"

    Add the following dependencies to your `pom.xml`:

    ```xml
    <dependencies>
        <!-- Core API -->
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-api</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Annotation processor (for @InjectLogger) -->
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-processor</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- Core implementation -->
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-core</artifactId>
            <version>1.0.0</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>io.github.dotbrains</groupId>
                            <artifactId>hermes-processor</artifactId>
                            <version>1.0.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
    ```

=== "Gradle"

    Add the following to your `build.gradle`:

    ```gradle
    dependencies {
        implementation 'io.github.dotbrains:hermes-api:1.0.0'
        annotationProcessor 'io.github.dotbrains:hermes-processor:1.0.0'
        runtimeOnly 'io.github.dotbrains:hermes-core:1.0.0'
    }
    ```

## Your First Logger

### Using @InjectLogger (Recommended)

The easiest way to use Hermes is with the `@InjectLogger` annotation:

```java
import io.github.dotbrains.InjectLogger;

@InjectLogger
public class UserService extends UserServiceHermesLogger {

    public void createUser(String username) {
        log.info("Creating user: {}", username);
        
        try {
            saveToDatabase(username);
            log.info("User {} created successfully", username);
        } catch (Exception e) {
            log.error("Failed to create user: {}", username, e);
        }
    }
}
```

!!! info "How it works"
    The annotation processor generates a base class `UserServiceHermesLogger` with a protected `Logger log` field. Your class simply extends it and uses `log` directly.

### Manual Logger Creation

If you prefer not to use annotation processing:

```java
import io.github.dotbrains.Logger;
import io.github.dotbrains.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void createUser(String username) {
        log.info("Creating user: {}", username);
    }
}
```

## Log Levels

Hermes supports five log levels (from least to most severe):

```java
log.trace("Detailed debug information");
log.debug("Debug information");
log.info("Informational messages");
log.warn("Warning messages");
log.error("Error messages");
```

## Parameterized Logging

Use `{}` placeholders for efficient parameterized logging:

```java
// Single parameter
log.info("User {} logged in", username);

// Multiple parameters
log.debug("Processing order {} for user {} with total {}", orderId, username, total);

// With exception (always last parameter)
log.error("Failed to process order {}", orderId, exception);
```

!!! tip "Performance"
    Parameterized logging avoids string concatenation, which is more efficient and only executed if the log level is enabled.

## Next Steps

- Learn about [Logger Injection](../user-guide/logger-injection.md) in detail
- Explore [MDC](../user-guide/mdc.md) for contextual logging
- Configure [Async Logging](../advanced/async-logging.md) for high-performance applications
- Set up [Spring Boot Integration](../integrations/spring-boot.md)
