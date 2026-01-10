# Log Levels

Hermes supports standard logging levels with compile-time level checking for optimal performance.

## Available Log Levels

From most to least severe:

- **ERROR**: Error events that might still allow the application to continue
- **WARN**: Potentially harmful situations
- **INFO**: Informational messages highlighting application progress
- **DEBUG**: Fine-grained informational events for debugging
- **TRACE**: Most detailed logging level

## Basic Logging

```java
log.error("Database connection failed");
log.warn("Cache size exceeding threshold");
log.info("Application started successfully");
log.debug("Processing request with ID: {}", requestId);
log.trace("Entering method calculateTotal()");
```

## Conditional Logging

For expensive operations, check the log level first to avoid unnecessary computation:

```java
if (log.isDebugEnabled()) {
    log.debug("User data: {}", expensiveUserDataSerialization());
}

if (log.isTraceEnabled()) {
    log.trace("Full request: {}", dumpCompleteRequest());
}
```

## Level Configuration

### Spring Boot (application.yml)

```yaml
hermes:
  level:
    root: INFO
    packages:
      io.github.dotbrains: DEBUG
      com.example.service: TRACE
```

### Programmatic Configuration

```java
Logger logger = LoggerFactory.getLogger(MyClass.class);
// Level filtering is handled by appender configuration
```

## Performance Considerations

Hermes optimizes logging with early exit:

1. Check if level is enabled
2. If disabled, return immediately (zero overhead)
3. If enabled, format message and create LogEvent

This means disabled log statements have near-zero performance impact.

## Best Practices

- Use **ERROR** for conditions that require immediate attention
- Use **WARN** for recoverable issues or deprecated usage
- Use **INFO** for important business-level events
- Use **DEBUG** for development and troubleshooting
- Use **TRACE** for very detailed diagnostic information
- Always use `isDebugEnabled()` / `isTraceEnabled()` for expensive operations
