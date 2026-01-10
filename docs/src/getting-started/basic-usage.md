# Basic Usage

Learn the fundamentals of using Hermes in your applications.

## Creating a Logger

There are two ways to create a logger in Hermes:

### Using @InjectLogger (Recommended)

```java
import io.github.dotbrains.InjectLogger;

@InjectLogger
public class UserService extends UserServiceHermesLogger {
    
    public void processUser(String username) {
        log.info("Processing user: {}", username);
    }
}
```

The annotation processor generates a base class with the logger field, eliminating boilerplate.

### Manual Logger Creation

```java
import io.github.dotbrains.Logger;
import io.github.dotbrains.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void processUser(String username) {
        log.info("Processing user: {}", username);
    }
}
```

## Log Levels

Hermes supports five log levels in order of increasing severity:

```java
log.trace("Entering method processUser()");
log.debug("User details: {}", userDetails);
log.info("User {} processed successfully", username);
log.warn("Cache miss for user {}", username);
log.error("Failed to process user {}", username, exception);
```

### Checking Log Levels

Avoid expensive operations when logging is disabled:

```java
if (log.isDebugEnabled()) {
    log.debug("Expensive result: {}", computeExpensiveData());
}

if (log.isTraceEnabled()) {
    log.trace("Stack trace: {}", generateStackTrace());
}
```

## Parameterized Logging

Use `{}` placeholders for efficient logging:

```java
// Single parameter
log.info("User {} logged in", username);

// Multiple parameters
log.info("Order {} placed by {} for ${}", orderId, username, amount);

// Up to 5 parameters
log.debug("Values: {}, {}, {}, {}, {}", v1, v2, v3, v4, v5);

// More than 5 parameters - use varargs
log.info("Many values: {}, {}, {}, {}, {}, {}", v1, v2, v3, v4, v5, v6);
```

!!! warning "Exception as last parameter"
    When logging with an exception, it must always be the last parameter:
    ```java
    // Correct ✓
    log.error("Failed to process {}", orderId, exception);
    
    // Incorrect ✗
    log.error("Failed to process {}", exception, orderId);
    ```

## Lazy Evaluation with Suppliers

For expensive operations, use suppliers to defer computation:

```java
import java.util.function.Supplier;

// Only executed if DEBUG is enabled
log.debug(() -> "Expensive data: " + generateExpensiveReport());

// Works with parameterized logging
log.debug("Result: {}", (Supplier<String>) this::expensiveComputation);
```

## Logging Exceptions

### Exception with Message

```java
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed for user {}", username, e);
}
```

This logs the message with parameters AND the full stack trace.

### Exception Only

```java
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed", e);
}
```

### Multiple Catch Blocks

```java
try {
    riskyOperation();
} catch (ValidationException e) {
    log.warn("Validation failed: {}", e.getMessage());
} catch (DatabaseException e) {
    log.error("Database error", e);
} catch (Exception e) {
    log.error("Unexpected error", e);
}
```

## Logger Naming

Loggers are typically named after the class:

```java
// Named after the class
Logger log = LoggerFactory.getLogger(UserService.class);

// Equivalent to
Logger log = LoggerFactory.getLogger("com.example.UserService");

// Custom name
Logger log = LoggerFactory.getLogger("SECURITY");
```

Logger names form a hierarchy:

```
com
├── example
│   ├── UserService
│   └── OrderService
└── another
    └── PaymentService
```

This hierarchy is used for package-level log level configuration.

## Static vs Instance Loggers

### Static Logger (Recommended)

```java
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
}
```

**Pros**: One logger per class, efficient
**Cons**: Not serializable (but rarely an issue)

### Instance Logger

```java
@InjectLogger
public class UserService extends UserServiceHermesLogger {
    // log field is protected and non-static
}
```

**Pros**: Serializable, works with inheritance
**Cons**: One logger per instance (slight overhead)

!!! tip "Choose static for most cases"
    Use static loggers unless you have a specific need for instance loggers (e.g., serialization, inheritance scenarios).

## Best Practices

### 1. Use Parameterized Logging

```java
// Good ✓
log.info("User {} logged in from {}", username, ipAddress);

// Bad ✗ - concatenation always executes
log.info("User " + username + " logged in from " + ipAddress);
```

### 2. Check Log Level for Expensive Operations

```java
// Good ✓
if (log.isDebugEnabled()) {
    log.debug("Details: {}", expensiveMethod());
}

// Bad ✗ - expensiveMethod() always executes
log.debug("Details: {}", expensiveMethod());
```

### 3. Use Appropriate Log Levels

- `TRACE`: Very detailed, typically method entry/exit
- `DEBUG`: Debugging information during development
- `INFO`: Important business events
- `WARN`: Unexpected but recoverable situations
- `ERROR`: Errors requiring attention

### 4. Include Context in Messages

```java
// Good ✓
log.error("Failed to send email to user {} for order {}", userId, orderId, e);

// Bad ✗ - not enough context
log.error("Email send failed", e);
```

## Next Steps

- Learn about [Logger Injection](../user-guide/logger-injection.md) in detail
- Explore [MDC](../user-guide/mdc.md) for contextual logging
- Use [Markers](../user-guide/markers.md) to categorize logs
- Configure [Appenders](../advanced/appenders.md) for output destinations
