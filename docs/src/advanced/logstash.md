# Logstash Integration

Hermes provides native Logstash integration for centralized logging with structured JSON events sent over TCP.

## Overview

`LogstashAppender` sends log events directly to Logstash using the Logstash JSON format:

- **Protocol**: TCP with JSON events
- **Format**: Logstash JSON (compatible with Logstash input)
- **Performance**: Async-capable with connection pooling
- **Reliability**: Automatic reconnection on failure

## Basic Setup

```java
import io.hermes.core.appender.LogstashAppender;

LogstashAppender appender = new LogstashAppender("localhost", 5000);
appender.setApplicationName("my-service");
appender.setEnvironment("production");
appender.start();

logger.addAppender(appender);
```

## Configuration

### Connection Settings

```java
LogstashAppender appender = new LogstashAppender("logstash.example.com", 5000);

// Timeout settings
appender.setConnectionTimeout(5000);  // 5 seconds
appender.setWriteTimeout(10000);      // 10 seconds

// Retry settings
appender.setReconnectDelay(1000);     // 1 second between retries
appender.setMaxReconnectAttempts(10); // Max retry attempts
```

### Application Metadata

```java
appender.setApplicationName("order-service");
appender.setEnvironment("production");
appender.setHostname("app-server-01");
appender.setVersion("1.2.3");
```

### Custom Fields

Add static fields to all log events:

```java
appender.addField("datacenter", "us-east-1");
appender.addField("cluster", "prod-cluster-1");
appender.addField("team", "platform");
```

## Logstash Configuration

### Input Configuration

Configure Logstash to receive JSON events:

```ruby
input {
  tcp {
    port => 5000
    codec => json_lines
    tags => ["hermes"]
  }
}

filter {
  # Parse timestamp
  date {
    match => ["timestamp", "ISO8601"]
    target => "@timestamp"
  }

  # Add fields
  mutate {
    add_field => {
      "[@metadata][index]" => "logs-%{application}-%{+YYYY.MM.dd}"
    }
  }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
    index => "%{[@metadata][index]}"
  }
}
```

### With SSL/TLS

```ruby
input {
  tcp {
    port => 5000
    codec => json_lines
    ssl_enable => true
    ssl_cert => "/path/to/cert.pem"
    ssl_key => "/path/to/key.pem"
    ssl_verify => true
  }
}
```

## Event Format

Hermes sends events in Logstash JSON format:

```json
{
  "@timestamp": "2024-01-10T10:30:45.123Z",
  "@version": "1",
  "message": "User logged in successfully",
  "logger_name": "io.github.dotbrains.AuthService",
  "level": "INFO",
  "level_value": 20000,
  "thread_name": "http-nio-8080-exec-1",
  "application": "my-service",
  "environment": "production",
  "hostname": "app-server-01",
  "version": "1.2.3",
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
  "@timestamp": "2024-01-10T10:30:45.123Z",
  "message": "Failed to process payment",
  "logger_name": "io.github.dotbrains.PaymentService",
  "level": "ERROR",
  "level_value": 40000,
  "exception": {
    "class": "java.lang.IllegalStateException",
    "message": "Payment gateway timeout",
    "stack_trace": "java.lang.IllegalStateException: Payment gateway timeout\n\tat io.github.dotbrains.PaymentService.process..."
  },
  "application": "order-service",
  "environment": "production"
}
```

## Spring Boot Integration

### application.yml

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
    reconnect-delay: 1000
    max-reconnect-attempts: 10
    custom-fields:
      datacenter: us-east-1
      cluster: prod-cluster-1
```

### Auto-Configuration

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

        // Add custom fields
        logstash.getCustomFields().forEach(appender::addField);

        appender.start();
        return appender;
    }
}
```

## High-Availability Setup

### Multiple Logstash Instances

```java
// Primary Logstash
LogstashAppender primary = new LogstashAppender("logstash1.example.com", 5000);
primary.setApplicationName("my-service");
primary.start();

// Failover Logstash
LogstashAppender failover = new LogstashAppender("logstash2.example.com", 5000);
failover.setApplicationName("my-service");
failover.start();

// Add both appenders
logger.addAppender(primary);
logger.addAppender(failover);
```

### With Async Logging

```java
LogstashAppender logstash = new LogstashAppender("logstash.example.com", 5000);
logstash.setApplicationName("my-service");

AsyncAppender async = new AsyncAppender(logstash);
async.setQueueSize(8192);
async.setBlockWhenFull(false);
async.start();

logger.addAppender(async);
```

## Filtering and Routing

### Level-Based Routing

Send only errors to Logstash:

```java
LogstashAppender appender = new LogstashAppender("logstash.example.com", 5000);
appender.setMinLevel(LogLevel.ERROR);  // Only ERROR and above
appender.start();
```

### Marker-Based Routing

Send specific markers to dedicated Logstash:

```java
// Security logs to dedicated Logstash
LogstashAppender securityAppender = new LogstashAppender("security-logstash.example.com", 5000);
securityAppender.setMarkerFilter("SECURITY");
securityAppender.start();

// Business logs to analytics Logstash
LogstashAppender businessAppender = new LogstashAppender("analytics-logstash.example.com", 5000);
businessAppender.setMarkerFilter("BUSINESS");
businessAppender.start();
```

## Monitoring and Health Checks

### Connection Status

```java
if (appender.isConnected()) {
    log.info("Logstash connection active");
} else {
    log.warn("Logstash connection down");
}
```

### Event Metrics

```java
long sentEvents = appender.getSentEventCount();
long failedEvents = appender.getFailedEventCount();
long droppedEvents = appender.getDroppedEventCount();

log.info("Logstash metrics - sent: {}, failed: {}, dropped: {}",
    sentEvents, failedEvents, droppedEvents);
```

### Spring Boot Actuator

```java
@Component
public class LogstashHealthIndicator implements HealthIndicator {

    private final LogstashAppender appender;

    @Override
    public Health health() {
        if (appender.isConnected()) {
            return Health.up()
                .withDetail("host", appender.getHost())
                .withDetail("port", appender.getPort())
                .withDetail("sent", appender.getSentEventCount())
                .build();
        } else {
            return Health.down()
                .withDetail("host", appender.getHost())
                .withDetail("port", appender.getPort())
                .withDetail("failed", appender.getFailedEventCount())
                .build();
        }
    }
}
```

## Troubleshooting

### Connection Failures

**Symptom**: Logs not appearing in Elasticsearch

**Checklist**:

1. Verify Logstash is running: `netstat -an | grep 5000`
2. Check network connectivity: `telnet logstash.example.com 5000`
3. Review Logstash logs: `tail -f /var/log/logstash/logstash-plain.log`
4. Verify firewall rules

### Performance Issues

**Symptom**: Slow application performance

**Solutions**:
```java
// Use async appender
AsyncAppender async = new AsyncAppender(logstashAppender);
async.setBlockWhenFull(false);  // Don't block on full queue
async.start();

// Reduce log volume
logstashAppender.setMinLevel(LogLevel.WARN);

// Increase timeouts
logstashAppender.setWriteTimeout(30000);  // 30 seconds
```

### Lost Events

**Symptom**: Missing log events

**Solutions**:
```java
// Enable blocking to prevent drops
asyncAppender.setBlockWhenFull(true);

// Increase queue size
asyncAppender.setQueueSize(65536);

// Check dropped event count
long dropped = appender.getDroppedEventCount();
```

## Best Practices

1. **Use async appender** - Prevents blocking on network I/O
2. **Configure timeouts** - Avoid indefinite hangs
3. **Monitor connection health** - Use health checks
4. **Add application metadata** - Environment, version, hostname
5. **Use structured MDC** - Better searchability in Elasticsearch
6. **Filter appropriately** - Don't send debug logs to production Logstash
7. **Plan for failures** - Logstash downtime shouldn't crash app
8. **Use multiple instances** - High availability with failover

## Complete Example

```java
// Production-ready Logstash configuration
LogstashAppender logstash = new LogstashAppender("logstash.example.com", 5000);

// Application metadata
logstash.setApplicationName("order-service");
logstash.setEnvironment("production");
logstash.setVersion("1.2.3");

// Custom fields
logstash.addField("datacenter", "us-east-1");
logstash.addField("team", "ecommerce");

// Connection settings
logstash.setConnectionTimeout(5000);
logstash.setWriteTimeout(10000);
logstash.setReconnectDelay(1000);
logstash.setMaxReconnectAttempts(10);

// Wrap with async for high performance
AsyncAppender async = new AsyncAppender(logstash);
async.setQueueSize(8192);
async.setBlockWhenFull(false);
async.start();

// Add to logger
logger.addAppender(async);

// MDC setup in request filter
MDC.put("requestId", UUID.randomUUID().toString());
MDC.put("userId", getCurrentUserId());

// Log with context
log.info("Order processed successfully");
```
