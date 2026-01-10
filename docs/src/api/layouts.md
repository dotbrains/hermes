# Layouts API

API reference for layouts that format log events into strings.

## Layout Interface

Base interface for all layouts.

### Package

```java
io.github.dotbrains.core.layout.Layout
```

### Methods

```java
String format(LogEvent event)
```

## PatternLayout

Formats log events using a configurable pattern string.

### Constructor

```java
PatternLayout(String pattern)
```

### Conversion Patterns

| Pattern | Description | Example |
|---------|-------------|----------|
| `%d{format}` | Date/timestamp | `2024-01-10 10:30:45.123` |
| `%date{format}` | Same as `%d` | |
| `%p` | Log level | `INFO` |
| `%level` | Same as `%p` | |
| `%-5p` | Left-padded level (5 chars) | `INFO ` |
| `%t` | Thread name | `http-nio-8080-exec-1` |
| `%thread` | Same as `%t` | |
| `%c` | Logger name | `io.github.dotbrains.UserService` |
| `%logger` | Same as `%c` | |
| `%c{n}` | Abbreviated logger name | `i.g.d.UserService` |
| `%logger{n}` | Same as `%c{n}` | |
| `%C` | Class name | `UserService` |
| `%class` | Same as `%C` | |
| `%M` | Method name | `createUser` |
| `%method` | Same as `%M` | |
| `%L` | Line number | `42` |
| `%line` | Same as `%L` | |
| `%m` | Log message | `User alice logged in` |
| `%msg` | Same as `%m` | |
| `%message` | Same as `%m` | |
| `%n` | Platform newline | `\n` or `\r\n` |
| `%ex` | Exception stack trace | Full stack trace |
| `%exception` | Same as `%ex` | |
| `%throwable` | Same as `%ex` | |
| `%X{key}` | MDC value for key | `req-12345` |
| `%mdc{key}` | Same as `%X{key}` | |
| `%marker` | Marker name | `SECURITY` |

### Date Format Patterns

```java
// ISO 8601
"%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"
// Output: 2024-01-10T10:30:45.123+00:00

// Simple date
"%d{yyyy-MM-dd}"
// Output: 2024-01-10

// Time only
"%d{HH:mm:ss.SSS}"
// Output: 10:30:45.123

// Custom format
"%d{dd/MMM/yyyy HH:mm:ss}"
// Output: 10/Jan/2024 10:30:45
```

### Common Patterns

#### Development

```java
PatternLayout layout = new PatternLayout(
    "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
);
```

Output:
```
10:30:45.123 INFO  i.g.d.h.UserService - User logged in
```

#### Production

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n%exception"
);
```

Output:
```
2024-01-10 10:30:45.123 [http-nio-8080-exec-1] INFO  io.github.dotbrains.UserService - User logged in
```

#### With MDC

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{requestId}] [%X{userId}] %-5level %logger - %msg%n"
);
```

Output:
```
2024-01-10 10:30:45.123 [req-12345] [user-789] INFO  io.github.dotbrains.UserService - User logged in
```

#### Compact

```java
PatternLayout layout = new PatternLayout(
    "%d{HH:mm:ss} %p %c{1} - %m%n"
);
```

Output:
```
10:30:45 INFO UserService - User logged in
```

### Example

```java
PatternLayout layout = new PatternLayout(
    "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
);

ConsoleAppender appender = new ConsoleAppender();
appender.setLayout(layout);
appender.start();
```

## JsonLayout

Formats log events as JSON for structured logging.

### Constructor

```java
JsonLayout()
JsonLayout(boolean prettyPrint)
```

### Methods

```java
void setIncludeTimestamp(boolean include)
void setIncludeLevel(boolean include)
void setIncludeThread(boolean include)
void setIncludeLogger(boolean include)
void setIncludeMessage(boolean include)
void setIncludeMdc(boolean include)
void setIncludeMarkers(boolean include)
void setIncludeStackTrace(boolean include)
void setTimestampFormat(String format)
void setPrettyPrint(boolean prettyPrint)
```

### Output Format

#### Compact (default)

```json
{"timestamp":"2024-01-10T10:30:45.123Z","level":"INFO","thread":"main","logger":"io.github.dotbrains.UserService","message":"User logged in","mdc":{"requestId":"req-12345"}}
```

#### Pretty Print

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "thread": "main",
  "logger": "io.github.dotbrains.UserService",
  "message": "User logged in",
  "mdc": {
    "requestId": "req-12345",
    "userId": "user-789"
  }
}
```

#### With Exception

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "ERROR",
  "logger": "io.github.dotbrains.OrderService",
  "message": "Order processing failed",
  "exception": {
    "class": "java.lang.IllegalStateException",
    "message": "Invalid order state",
    "stackTrace": [
      "io.github.dotbrains.OrderService.process(OrderService.java:45)",
      "io.github.dotbrains.OrderController.createOrder(OrderController.java:78)"
    ]
  }
}
```

### Example

```java
// Compact JSON for production
JsonLayout layout = new JsonLayout();
layout.setIncludeStackTrace(true);
layout.setIncludeMdc(true);
layout.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

FileAppender appender = new FileAppender("app.json");
appender.setLayout(layout);
appender.start();
```

## Custom Layout Implementation

### Basic Template

```java
public class CustomLayout implements Layout {
    
    @Override
    public String format(LogEvent event) {
        return String.format("[%s] %s: %s%n",
            event.level(),
            event.loggerName(),
            event.message()
        );
    }
}
```

### CSV Layout Example

```java
public class CsvLayout implements Layout {
    private static final String DELIMITER = ",";
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public String format(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        
        // Timestamp
        sb.append(quote(FORMATTER.format(event.timestamp())));
        sb.append(DELIMITER);
        
        // Level
        sb.append(quote(event.level().toString()));
        sb.append(DELIMITER);
        
        // Logger
        sb.append(quote(event.loggerName()));
        sb.append(DELIMITER);
        
        // Thread
        sb.append(quote(Thread.currentThread().getName()));
        sb.append(DELIMITER);
        
        // Message
        sb.append(quote(event.message()));
        sb.append("\n");
        
        return sb.toString();
    }
    
    private String quote(String value) {
        if (value == null) {
            return "\"\"";
        }
        // Escape quotes
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
```

Output:
```csv
"2024-01-10 10:30:45.123","INFO","io.github.dotbrains.UserService","main","User logged in"
```

### XML Layout Example

```java
public class XmlLayout implements Layout {
    
    @Override
    public String format(LogEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("<logEvent>");
        sb.append("<timestamp>").append(event.timestamp()).append("</timestamp>");
        sb.append("<level>").append(event.level()).append("</level>");
        sb.append("<logger>").append(escape(event.loggerName())).append("</logger>");
        sb.append("<message>").append(escape(event.message())).append("</message>");
        
        if (event.mdc() != null && !event.mdc().isEmpty()) {
            sb.append("<mdc>");
            event.mdc().forEach((key, value) -> {
                sb.append("<entry key=\"").append(escape(key)).append("\">");
                sb.append(escape(value));
                sb.append("</entry>");
            });
            sb.append("</mdc>");
        }
        
        sb.append("</logEvent>\n");
        return sb.toString();
    }
    
    private String escape(String value) {
        if (value == null) return "";
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
}
```

## Layout Performance

### Overhead Comparison

| Layout | Overhead | Best For |
|--------|----------|----------|
| SimpleLayout | Very Low (~10ns) | Development |
| PatternLayout | Low (~50-100ns) | Production files |
| JsonLayout | Medium (~100-200ns) | Log aggregation |
| Custom | Varies | Specific needs |

### Optimization Tips

1. **Reuse StringBuilder** - Use ThreadLocal for zero allocation
2. **Cache formatters** - Date formatters are expensive to create
3. **Minimize string operations** - Avoid concatenation
4. **Profile custom layouts** - Measure performance impact

### Example: Optimized Layout

```java
public class OptimizedLayout implements Layout {
    private static final ThreadLocal<StringBuilder> BUFFER = 
        ThreadLocal.withInitial(() -> new StringBuilder(256));
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public String format(LogEvent event) {
        StringBuilder sb = BUFFER.get();
        sb.setLength(0);  // Reset
        
        // Build formatted string
        sb.append(FORMATTER.format(event.timestamp()));
        sb.append(" ");
        sb.append(event.level());
        sb.append(" ");
        sb.append(event.message());
        sb.append("\n");
        
        return sb.toString();  // Only allocation
    }
}
```

## Best Practices

1. **Choose appropriate layout** - Pattern for humans, JSON for machines
2. **Include timestamps** - Essential for debugging
3. **Add context** - Use MDC values in pattern
4. **Keep patterns readable** - Balance detail vs. clarity
5. **Test output format** - Verify with actual log events
6. **Consider log aggregation** - Use JSON for centralized logging
7. **Profile performance** - Measure layout overhead
