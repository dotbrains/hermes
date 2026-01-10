# JSON Logging

JSON logging outputs structured log data ideal for log aggregation, analysis, and centralized logging systems.

## Overview

JsonLayout formats log events as JSON documents with consistent structure:

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
  }
}
```

## Basic Setup

```java
import io.hermes.core.layout.JsonLayout;
import io.hermes.core.appender.FileAppender;

JsonLayout layout = new JsonLayout();
FileAppender appender = new FileAppender("app.json");
appender.setLayout(layout);
appender.start();

logger.addAppender(appender);
```

## Configuration

### Full Configuration

```java
JsonLayout layout = new JsonLayout();

// Include/exclude components
layout.setIncludeTimestamp(true);      // ISO 8601 timestamp
layout.setIncludeLevel(true);          // Log level
layout.setIncludeThread(true);         // Thread name
layout.setIncludeLogger(true);         // Logger name
layout.setIncludeMessage(true);        // Log message
layout.setIncludeMdc(true);            // MDC context
layout.setIncludeMarkers(true);        // Markers
layout.setIncludeStackTrace(true);     // Exception stack traces

// Timestamp format
layout.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

// Pretty print (debugging only)
layout.setPrettyPrint(false);  // Compact (production)
```

### Minimal Configuration

For high-throughput scenarios:

```java
JsonLayout layout = new JsonLayout();
layout.setIncludeThread(false);
layout.setIncludeLogger(false);
layout.setIncludeMdc(false);
// Only timestamp, level, message
```

## Output Structure

### Standard Log Event

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "thread": "main",
  "logger": "io.github.dotbrains.UserService",
  "message": "Processing user registration"
}
```

### With MDC Context

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "io.github.dotbrains.OrderService",
  "message": "Order created successfully",
  "mdc": {
    "requestId": "req-12345",
    "userId": "user-789",
    "orderId": "order-456"
  }
}
```

### With Markers

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "WARN",
  "thread": "http-nio-8080-exec-2",
  "logger": "io.github.dotbrains.AuthService",
  "message": "Failed login attempt",
  "marker": "SECURITY",
  "mdc": {
    "requestId": "req-12346",
    "username": "attacker",
    "ip": "192.168.1.100"
  }
}
```

### With Exception

```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "ERROR",
  "thread": "http-nio-8080-exec-3",
  "logger": "io.github.dotbrains.PaymentService",
  "message": "Payment processing failed",
  "exception": {
    "class": "java.lang.IllegalStateException",
    "message": "Payment gateway timeout",
    "stackTrace": [
      "io.github.dotbrains.PaymentService.processPayment(PaymentService.java:45)",
      "io.github.dotbrains.OrderController.checkout(OrderController.java:78)",
      "jdk.internal.reflect.GeneratedMethodAccessor42.invoke(Unknown Source)"
    ],
    "cause": {
      "class": "java.net.SocketTimeoutException",
      "message": "Read timed out",
      "stackTrace": [
        "java.net.SocketInputStream.socketRead0(Native Method)",
        "java.net.SocketInputStream.read(SocketInputStream.java:162)"
      ]
    }
  },
  "mdc": {
    "requestId": "req-12347",
    "orderId": "order-789"
  }
}
```

## Integration with Log Aggregation Systems

### Elasticsearch (ELK Stack)

```java
// Output to file for Filebeat
JsonLayout layout = new JsonLayout();
layout.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

FileAppender appender = new FileAppender("app.json");
appender.setLayout(layout);
appender.start();
```

Filebeat configuration:
```yaml
filebeat.inputs:
  - type: log
    paths:
      - /var/log/app.json
    json.keys_under_root: true
    json.add_error_key: true

output.elasticsearch:
  hosts: ["localhost:9200"]
```

### Logstash

```java
// Send directly to Logstash
LogstashAppender appender = new LogstashAppender("localhost", 5000);
appender.setApplicationName("my-service");
appender.setEnvironment("production");
appender.start();
```

### Splunk

```java
// HTTP Event Collector (HEC)
JsonLayout layout = new JsonLayout();

// Custom HTTP appender for Splunk HEC
HttpAppender appender = new HttpAppender(
    "https://splunk.example.com:8088/services/collector",
    "Splunk YOUR-HEC-TOKEN"
);
appender.setLayout(layout);
appender.start();
```

### CloudWatch Logs

```java
// CloudWatch Logs appender with JSON
JsonLayout layout = new JsonLayout();

CloudWatchAppender appender = new CloudWatchAppender(
    "my-log-group",
    "my-log-stream"
);
appender.setLayout(layout);
appender.start();
```

## Querying JSON Logs

### Using `jq`

```bash
# Extract all ERROR logs
cat app.json | jq 'select(.level == "ERROR")'

# Get unique loggers
cat app.json | jq -r '.logger' | sort -u

# Filter by MDC field
cat app.json | jq 'select(.mdc.requestId == "req-12345")'

# Extract messages and timestamps
cat app.json | jq -r '"\(.timestamp) \(.message)"'

# Count by log level
cat app.json | jq -r '.level' | sort | uniq -c
```

### Elasticsearch Queries

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "level": "ERROR" } },
        { "range": { "timestamp": { "gte": "now-1h" } } }
      ]
    }
  }
}
```

### Splunk Queries

```
source="app.json" level=ERROR 
| stats count by logger
| sort -count
```

## Custom Fields

Add application-specific fields by extending MDC:

```java
MDC.put("environment", "production");
MDC.put("version", "1.2.3");
MDC.put("region", "us-east-1");

log.info("Application started");
```

Output:
```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "message": "Application started",
  "mdc": {
    "environment": "production",
    "version": "1.2.3",
    "region": "us-east-1"
  }
}
```

## Performance Considerations

### Compact Format

Use compact (non-pretty) format for production:

```java
layout.setPrettyPrint(false);  // One line per event
```

### Selective Fields

Disable unused fields to reduce overhead:

```java
JsonLayout layout = new JsonLayout();
layout.setIncludeThread(false);  // Skip if not needed
layout.setIncludeLogger(false);  // Skip if not needed
```

### Async Logging

Combine with AsyncAppender for high throughput:

```java
JsonLayout layout = new JsonLayout();
FileAppender fileAppender = new FileAppender("app.json");
fileAppender.setLayout(layout);

AsyncAppender asyncAppender = new AsyncAppender(fileAppender);
asyncAppender.setQueueSize(8192);
asyncAppender.start();
```

## Best Practices

1. **Use structured MDC** - Add context as MDC fields, not in messages
2. **Consistent field names** - Standardize across services
3. **Include trace IDs** - For distributed tracing correlation
4. **Use markers for categories** - Security, audit, business events
5. **Validate JSON output** - Test with `jq` or validators
6. **Compact format in production** - One line per event
7. **Index important fields** - Configure Elasticsearch/Splunk indexes
8. **Retention policies** - Archive or delete old logs

## Example: Microservices Setup

```java
// Configure structured logging for microservices
JsonLayout layout = new JsonLayout();
layout.setTimestampFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

// Add service metadata to MDC
MDC.put("service", "order-service");
MDC.put("version", "1.2.3");
MDC.put("environment", "production");
MDC.put("region", "us-east-1");

// File appender with async
FileAppender fileAppender = new FileAppender("/var/log/order-service.json");
fileAppender.setLayout(layout);

AsyncAppender asyncAppender = new AsyncAppender(fileAppender);
asyncAppender.setQueueSize(8192);
asyncAppender.start();

logger.addAppender(asyncAppender);
```

Request handling:
```java
@Filter
public class TracingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        try {
            MDC.put("traceId", generateTraceId());
            MDC.put("spanId", generateSpanId());
            MDC.put("requestPath", ((HttpServletRequest) req).getRequestURI());
            
            chain.doFilter(req, res);
        } finally {
            MDC.remove("traceId");
            MDC.remove("spanId");
            MDC.remove("requestPath");
        }
    }
}
```

Output:
```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "logger": "io.github.dotbrains.OrderController",
  "message": "Order created",
  "mdc": {
    "service": "order-service",
    "version": "1.2.3",
    "environment": "production",
    "region": "us-east-1",
    "traceId": "abc123",
    "spanId": "xyz789",
    "requestPath": "/api/orders"
  }
}
```
