# Markers

Markers allow you to tag log statements with special labels for filtering and routing purposes.

## What are Markers?

Markers are named tags attached to log events that can be used to:

- Filter logs by category or type
- Route specific logs to different appenders
- Trigger alerts based on marker presence
- Add semantic meaning to log statements

## Basic Usage

```java
import io.hermes.Marker;
import io.hermes.MarkerFactory;

// Create markers
Marker securityMarker = MarkerFactory.getMarker("SECURITY");
Marker performanceMarker = MarkerFactory.getMarker("PERFORMANCE");

// Use markers in log statements
log.info(securityMarker, "User {} logged in", username);
log.warn(securityMarker, "Failed login attempt for {}", username);
log.debug(performanceMarker, "Query took {} ms", duration);
```

## Hierarchical Markers

Markers can have parent markers for hierarchical organization:

```java
Marker sqlMarker = MarkerFactory.getMarker("SQL");
Marker slowQueryMarker = MarkerFactory.getMarker("SLOW_QUERY");
slowQueryMarker.add(sqlMarker);  // SLOW_QUERY is a child of SQL

// This log matches both SLOW_QUERY and SQL filters
log.warn(slowQueryMarker, "Query exceeded threshold: {} ms", duration);
```

## Common Marker Patterns

### Security Events

```java
Marker SECURITY = MarkerFactory.getMarker("SECURITY");
Marker AUDIT = MarkerFactory.getMarker("AUDIT");

log.info(SECURITY, "Access granted to resource {} for user {}", resource, user);
log.warn(AUDIT, "Configuration changed by admin {}", adminId);
```

### Performance Monitoring

```java
Marker PERFORMANCE = MarkerFactory.getMarker("PERFORMANCE");
Marker SLOW_OPERATION = MarkerFactory.getMarker("SLOW_OPERATION");

if (duration > threshold) {
    log.warn(SLOW_OPERATION, "Operation took {} ms", duration);
}
```

### Business Events

```java
Marker ORDER = MarkerFactory.getMarker("ORDER");
Marker PAYMENT = MarkerFactory.getMarker("PAYMENT");

log.info(ORDER, "Order {} created for customer {}", orderId, customerId);
log.info(PAYMENT, "Payment processed: amount={}, method={}", amount, method);
```

## Filtering by Markers

Configure appenders to filter based on markers:

```java
// Example: Send security logs to a separate file
FileAppender securityAppender = new FileAppender("security.log");
securityAppender.setMarkerFilter("SECURITY");

// Example: Send all logs except performance markers
ConsoleAppender console = new ConsoleAppender();
console.setExcludeMarker("PERFORMANCE");
```

## Marker API

### MarkerFactory

- `MarkerFactory.getMarker(String name)` - Get or create a marker
- `MarkerFactory.exists(String name)` - Check if marker exists
- `MarkerFactory.detachMarker(String name)` - Remove marker from registry

### Marker Methods

- `marker.getName()` - Get marker name
- `marker.add(Marker child)` - Add child marker
- `marker.remove(Marker child)` - Remove child marker
- `marker.hasChildren()` - Check if marker has children
- `marker.contains(Marker other)` - Check if marker contains another (recursively)

## Pattern Layout Integration

Include marker names in log output:

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %marker %logger - %msg%n"
);
```

Example output:
```
2024-01-10 10:30:45 [http-nio-8080-exec-1] INFO  SECURITY com.example.AuthService - User alice logged in
2024-01-10 10:30:46 [http-nio-8080-exec-2] WARN  SLOW_QUERY com.example.OrderService - Query exceeded threshold: 1500 ms
```

## JSON Layout Integration

Markers appear as fields in JSON output:

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "marker": "SECURITY",
  "logger": "com.example.AuthService",
  "message": "User alice logged in"
}
```

## Best Practices

1. **Use consistent naming** - Define markers as constants
2. **Document marker meanings** - Create a marker registry for your team
3. **Don't overuse** - Too many markers reduce their effectiveness
4. **Use hierarchies** - Organize related markers with parent/child relationships
5. **Filter strategically** - Route critical markers to dedicated appenders
6. **Combine with MDC** - Use markers for category, MDC for context

## Example: Comprehensive Marker Strategy

```java
public class AppMarkers {
    // Top-level categories
    public static final Marker SECURITY = MarkerFactory.getMarker("SECURITY");
    public static final Marker BUSINESS = MarkerFactory.getMarker("BUSINESS");
    public static final Marker TECHNICAL = MarkerFactory.getMarker("TECHNICAL");

    // Security subcategories
    public static final Marker AUTH = MarkerFactory.getMarker("AUTH");
    public static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

    static {
        AUTH.add(SECURITY);
        AUDIT.add(SECURITY);
    }

    // Usage
    public void login(String username) {
        log.info(AUTH, "User {} logged in", username);
    }

    public void changeConfig(String setting, String value) {
        log.info(AUDIT, "Config changed: {}={}", setting, value);
    }
}
```

## Use Cases

- **Security monitoring**: Tag authentication, authorization, and audit events
- **Performance tracking**: Mark slow operations, expensive queries
- **Business metrics**: Tag revenue events, user actions, conversions
- **Alerting**: Trigger alerts based on specific markers
- **Compliance**: Mark logs that require retention or special handling
- **Debugging**: Temporarily mark subsystem logs for focused troubleshooting
