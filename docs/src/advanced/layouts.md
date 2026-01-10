# Layouts

Layouts format log events into strings. Hermes provides flexible pattern-based and JSON layouts.

## PatternLayout

The most common layout, using conversion patterns similar to Log4j and Logback.

### Basic Usage

```java
import io.hermes.core.layout.PatternLayout;

PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
);
```

### Conversion Patterns

| Pattern | Description | Example |
|---------|-------------|---------|
| `%d{format}` | Date/time | `2024-01-10 10:30:45` |
| `%thread` | Thread name | `http-nio-8080-exec-1` |
| `%level` | Log level | `INFO` |
| `%-5level` | Left-padded level | `INFO ` |
| `%logger` | Full logger name | `io.github.dotbrains.UserService` |
| `%logger{n}` | Abbreviated logger | `i.g.d.UserService` |
| `%msg` | Log message | `User logged in` |
| `%n` | Newline | (platform-specific) |
| `%exception` | Exception stack trace | Full stack trace |
| `%X{key}` | MDC value | `req-12345` |
| `%marker` | Marker name | `SECURITY` |

### Date Format Options

```java
// ISO 8601 format
"%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"

// Simple date
"%d{yyyy-MM-dd}"

// Time only
"%d{HH:mm:ss.SSS}"

// Default (ISO 8601)
"%d"
```

### Logger Name Abbreviation

```java
// Full name
"%logger"  
// io.github.dotbrains.hermes.UserService

// Abbreviated to 36 chars
"%logger{36}"  
// i.g.d.h.UserService

// Abbreviated to length
"%logger{20}"  
// i.g.d.h.UserService (max 20 chars)
```

### Common Patterns

#### Development

```java
PatternLayout devLayout = new PatternLayout(
    "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
);
```

Example output:
```
10:30:45.123 INFO  i.g.d.h.UserService - User alice logged in
```

#### Production

```java
PatternLayout prodLayout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{requestId}] %logger - %msg%n%exception"
);
```

Example output:
```
2024-01-10 10:30:45.123 [http-nio-8080-exec-1] INFO  [req-12345] io.github.dotbrains.UserService - User alice logged in
```

#### Compact

```java
PatternLayout compactLayout = new PatternLayout(
    "%d{HH:mm:ss} %-5level %logger{20} - %msg%n"
);
```

## JsonLayout

Outputs structured JSON for log aggregation and analysis.

### Basic Usage

```java
import io.hermes.core.layout.JsonLayout;

JsonLayout layout = new JsonLayout();
layout.setIncludeStackTrace(true);
layout.setIncludeMdc(true);
layout.setIncludeMarkers(true);
```

### Output Format

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "io.github.dotbrains.UserService",
  "message": "User alice logged in",
  "mdc": {
    "requestId": "req-12345",
    "userId": "user-789"
  },
  "marker": "SECURITY"
}
```

### With Exception

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "ERROR",
  "thread": "http-nio-8080-exec-1",
  "logger": "io.github.dotbrains.OrderService",
  "message": "Failed to process order",
  "exception": {
    "class": "java.lang.IllegalStateException",
    "message": "Order already processed",
    "stackTrace": [
      "io.github.dotbrains.OrderService.process(OrderService.java:45)",
      "io.github.dotbrains.OrderController.createOrder(OrderController.java:78)"
    ]
  },
  "mdc": {
    "requestId": "req-12345",
    "orderId": "order-456"
  }
}
```

### Configuration Options

```java
JsonLayout layout = new JsonLayout();

// Include/exclude components
layout.setIncludeTimestamp(true);      // Default: true
layout.setIncludeLevel(true);          // Default: true
layout.setIncludeThread(true);         // Default: true
layout.setIncludeLogger(true);         // Default: true
layout.setIncludeMessage(true);        // Default: true
layout.setIncludeMdc(true);            // Default: true
layout.setIncludeMarkers(true);        // Default: true
layout.setIncludeStackTrace(true);     // Default: true

// Timestamp format
layout.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  // ISO 8601

// Pretty print (not recommended for production)
layout.setPrettyPrint(false);  // Default: false (compact)
```

## Custom Layouts

Implement the `Layout` interface for custom formatting:

```java
import io.hermes.core.layout.Layout;
import io.hermes.core.LogEvent;

public class CustomLayout implements Layout {
    
    @Override
    public String format(LogEvent event) {
        // Custom formatting logic
        return String.format("[%s] %s: %s",
            event.level(),
            event.loggerName(),
            event.message()
        );
    }
}
```

### Example: CSV Layout

```java
public class CsvLayout implements Layout {
    
    @Override
    public String format(LogEvent event) {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n",
            event.timestamp(),
            event.level(),
            event.loggerName(),
            event.message().replace("\"", "\"\"")  // Escape quotes
        );
    }
}
```

Output:
```csv
"2024-01-10T10:30:45.123Z","INFO","io.github.dotbrains.UserService","User logged in"
```

## Configuring Layouts with Appenders

```java
// Pattern layout for console
ConsoleAppender console = new ConsoleAppender();
console.setLayout(new PatternLayout("%d{HH:mm:ss} %-5level %msg%n"));
console.start();

// JSON layout for file
FileAppender file = new FileAppender("app.json");
file.setLayout(new JsonLayout());
file.start();

// Add to logger
logger.addAppender(console);
logger.addAppender(file);
```

## Performance Considerations

### PatternLayout

- Efficient for simple patterns
- Date formatting can be expensive (caching helps)
- ThreadLocal StringBuilder for zero-allocation formatting

### JsonLayout

- Slightly more overhead than PatternLayout
- Efficient for structured logging
- No string concatenation (uses JSON builder)
- Ideal for log aggregation systems

## Best Practices

1. **Use appropriate layouts per appender** - Console: compact pattern, File: detailed pattern
2. **Include context in production** - MDC, thread, timestamps
3. **Use JSON for centralized logging** - ELK, Splunk, CloudWatch
4. **Keep patterns consistent** - Define standard patterns for your team
5. **Include exception stack traces** - Use `%exception` or JsonLayout
6. **Avoid expensive operations** - Don't call methods in MDC/markers
7. **Test layouts** - Verify output format matches expectations

## Layout Examples

### Microservices Pattern

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{service}] [%X{traceId}] [%X{spanId}] %logger{36} - %msg%n"
);
```

### Security Audit Pattern

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} %marker [%X{userId}] [%X{ip}] %-5level %logger - %msg%n"
);
```

### High-Performance Pattern

```java
// Minimal overhead - timestamp and message only
PatternLayout layout = new PatternLayout("%d{HH:mm:ss} %msg%n");
```
