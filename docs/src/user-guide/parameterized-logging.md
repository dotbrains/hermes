# Parameterized Logging

Hermes uses SLF4J-style `{}` placeholders for efficient message formatting with zero-allocation optimization.

## Basic Usage

Use `{}` as a placeholder for parameters:

```java
log.info("User {} logged in from IP {}", username, ipAddress);
log.debug("Processing order {} for customer {}", orderId, customerId);
log.warn("Retry attempt {} failed after {} ms", attempt, duration);
```

## Multiple Parameters

Support for any number of parameters:

```java
log.info("Request {} from {} completed in {} ms with status {}",
    requestId, userId, duration, statusCode);
```

## Exception Logging

Always pass the exception as the last parameter (not counted as a placeholder):

```java
try {
    processOrder(order);
} catch (Exception e) {
    log.error("Failed to process order {}", orderId, e);
}
```

The exception will be formatted with its stack trace automatically.

## Performance Optimization

Hermes uses ThreadLocal StringBuilder for zero-allocation formatting:

### How It Works

1. Check if log level is enabled (early exit if disabled)
2. Retrieve ThreadLocal StringBuilder (reused across calls)
3. Format message by replacing `{}` placeholders
4. Create immutable LogEvent with formatted message
5. Reset StringBuilder for next use

### Benefits

- **Zero allocation** for disabled log statements
- **Single allocation** (LogEvent) for enabled statements
- **ThreadLocal reuse** eliminates StringBuilder creation overhead

## Best Practices

### ✅ Do This

```java
// Efficient - formatting only happens if DEBUG is enabled
log.debug("Processing {} items in batch {}", itemCount, batchId);

// Conditional for expensive operations
if (log.isDebugEnabled()) {
    log.debug("User details: {}", user.toDetailedString());
}
```

### ❌ Avoid This

```java
// Inefficient - string concatenation happens regardless of level
log.debug("Processing " + itemCount + " items in batch " + batchId);

// Inefficient - method called even if DEBUG is disabled
log.debug("User details: {}", user.expensiveSerializationMethod());
```

## Formatting Rules

- `{}` is replaced with the corresponding parameter in order
- Extra parameters are ignored
- Missing parameters leave `{}` in the output
- `null` parameters are rendered as the string "null"
- Arrays and collections are formatted using `toString()`

## Examples

```java
// Simple replacement
log.info("Hello {}", "World");
// Output: Hello World

// Multiple placeholders
log.info("{} + {} = {}", 1, 2, 3);
// Output: 1 + 2 = 3

// With exception
log.error("Failed to connect to {}", hostname, exception);
// Output: Failed to connect to db.example.com
//         java.net.ConnectException: Connection refused
//         [stack trace...]

// Null handling
log.info("User: {}, Email: {}", username, null);
// Output: User: alice, Email: null
```
