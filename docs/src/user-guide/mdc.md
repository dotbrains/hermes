# MDC (Mapped Diagnostic Context)

MDC provides thread-local storage for contextual information that should be included in all log statements within a thread.

## What is MDC?

MDC is a map of key-value pairs attached to the current thread. Values set in the MDC are automatically included in log output, making it easy to track requests, users, or transactions across multiple log statements.

## Basic Usage

```java
import io.hermes.MDC;

// Set values
MDC.put("requestId", "req-12345");
MDC.put("userId", "user-789");

// All log statements in this thread will include these values
log.info("Processing request");  // Includes requestId and userId
log.debug("Validating input");   // Also includes requestId and userId

// Remove a specific value
MDC.remove("userId");

// Clear all MDC values
MDC.clear();
```

## Web Request Example

Typical usage in a web filter or interceptor:

```java
@Component
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            // Set MDC values at the start of the request
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("clientIp", request.getRemoteAddr());

            // Process the request - all logs will include these values
            chain.doFilter(request, response);
        } finally {
            // Always clear MDC after request completes
            MDC.clear();
        }
    }
}
```

## Async Context Propagation

When using async processing, MDC context is NOT automatically propagated. You must manually copy it:

```java
// Capture MDC from current thread
Map<String, String> contextMap = MDC.getCopyOfContextMap();

CompletableFuture.runAsync(() -> {
    try {
        // Restore MDC in async thread
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }

        log.info("Processing async task");  // Now includes MDC context
    } finally {
        MDC.clear();
    }
});
```

## Pattern Layout Integration

Include MDC values in log output using `%X{key}` in your pattern:

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{userId}] %logger - %msg%n"
);
```

Example output:
```
2024-01-10 10:30:45 [http-nio-8080-exec-1] INFO  [req-12345] [user-789] com.example.UserService - User logged in
```

## JSON Layout Integration

MDC values are automatically included as fields in JSON output:

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.example.UserService",
  "message": "User logged in",
  "mdc": {
    "requestId": "req-12345",
    "userId": "user-789"
  }
}
```

## API Methods

- `MDC.put(String key, String value)` - Add or update a value
- `MDC.get(String key)` - Retrieve a value
- `MDC.remove(String key)` - Remove a specific key
- `MDC.clear()` - Clear all values for current thread
- `MDC.getCopyOfContextMap()` - Get a copy of all MDC values
- `MDC.setContextMap(Map<String, String>)` - Set MDC from a map

## Best Practices

1. **Always clear MDC** in a `finally` block to prevent memory leaks
2. **Use request IDs** to correlate logs across distributed systems
3. **Propagate context** manually when using async/reactive programming
4. **Keep values simple** - use strings, not complex objects
5. **Document MDC keys** used in your application for consistency

## Thread Safety

MDC is thread-local by design:

- Each thread has its own MDC map
- Values set in one thread don't affect other threads
- No synchronization needed for MDC operations
- Must manually propagate to new threads (e.g., thread pools)

## Common Use Cases

- Request tracking in web applications
- Transaction IDs in database operations
- User context in security-sensitive operations
- Correlation IDs in microservices
- Session tracking
- Tenant identification in multi-tenant systems
