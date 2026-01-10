# Appenders

Appenders control where log events are written. Hermes provides several built-in appenders with support for custom implementations.

## Built-in Appenders

### ConsoleAppender

Writes log events to stdout/stderr:

```java
import io.hermes.core.appender.ConsoleAppender;
import io.hermes.core.layout.PatternLayout;

ConsoleAppender appender = new ConsoleAppender();
appender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5level %logger - %msg%n"));
appender.start();
```

### FileAppender

Writes log events to a file:

```java
import io.hermes.core.appender.FileAppender;

FileAppender appender = new FileAppender("application.log");
appender.setLayout(new PatternLayout("%d %-5level %logger - %msg%n"));
appender.setAppend(true);  // Append to existing file (default: true)
appender.start();
```

### RollingFileAppender

Automatically rolls log files based on size or time:

```java
import io.hermes.core.appender.RollingFileAppender;

RollingFileAppender appender = new RollingFileAppender(
    "logs/app.log",
    "logs/app-%d{yyyy-MM-dd}.log.gz"  // Pattern for archived files
);

// Size-based rolling
appender.setMaxFileSize("10MB");
appender.setMaxHistory(30);  // Keep 30 days of logs

// Time-based rolling (daily)
appender.setRollingPolicy("daily");

appender.start();
```

### AsyncAppender

High-throughput async logging using LMAX Disruptor:

```java
import io.hermes.core.appender.AsyncAppender;

// Wrap another appender for async processing
FileAppender fileAppender = new FileAppender("app.log");
fileAppender.setLayout(new PatternLayout("%d %-5level %msg%n"));

AsyncAppender asyncAppender = new AsyncAppender(fileAppender);
asyncAppender.setQueueSize(8192);  // Ring buffer size (power of 2)
asyncAppender.setBlockWhenFull(false);  // Drop events when full
asyncAppender.start();
```

### LogstashAppender

Sends structured logs to Logstash via TCP:

```java
import io.hermes.core.appender.LogstashAppender;

LogstashAppender appender = new LogstashAppender("localhost", 5000);
appender.setApplicationName("my-service");
appender.setEnvironment("production");
appender.start();
```

## Appender Lifecycle

All appenders implement a common lifecycle:

```java
// Start the appender (required before use)
appender.start();

// Check if started
if (appender.isStarted()) {
    // Appender is ready
}

// Stop the appender (flushes buffers, closes resources)
appender.stop();
```

## Configuring Multiple Appenders

Route logs to multiple destinations:

```java
import io.hermes.core.HermesLogger;

HermesLogger logger = (HermesLogger) LoggerFactory.getLogger(MyClass.class);

// Console appender
ConsoleAppender console = new ConsoleAppender();
console.setLayout(new PatternLayout("%d %-5level %msg%n"));
console.start();

// File appender
FileAppender file = new FileAppender("app.log");
file.setLayout(new PatternLayout("%d %-5level %logger - %msg%n"));
file.start();

// Add both appenders
logger.addAppender(console);
logger.addAppender(file);
```

## Filtering Appenders

### Level Filtering

Only log events at or above a certain level:

```java
FileAppender errorFile = new FileAppender("errors.log");
errorFile.setMinLevel(LogLevel.ERROR);  // Only ERROR and above
errorFile.start();
```

### Marker Filtering

Route logs based on markers:

```java
FileAppender securityLog = new FileAppender("security.log");
securityLog.setMarkerFilter("SECURITY");  // Only logs with SECURITY marker
securityLog.start();
```

## Custom Appenders

Implement the `Appender` interface to create custom appenders:

```java
import io.hermes.core.appender.Appender;
import io.hermes.core.LogEvent;

public class CustomAppender implements Appender {
    private volatile boolean started = false;
    private Layout layout;

    @Override
    public void append(LogEvent event) {
        if (!started) return;

        String formattedMessage = layout.format(event);
        // Write to your custom destination
    }

    @Override
    public void start() {
        // Initialize resources (open connections, files, etc.)
        started = true;
    }

    @Override
    public void stop() {
        // Clean up resources
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
```

## Performance Considerations

### Synchronous Appenders

Console, File, and RollingFile appenders are synchronous:

- Calling thread blocks until write completes
- Simple and reliable
- Lower throughput under high load

### Asynchronous Appenders

AsyncAppender provides high throughput:

- Calling thread publishes to ring buffer (non-blocking)
- Background thread writes to wrapped appender
- Trade-off: Risk of log loss on crash
- Ideal for high-volume logging

## Best Practices

1. **Always call `start()`** - Appenders must be started before use
2. **Always call `stop()`** - Ensures buffers are flushed and resources closed
3. **Use AsyncAppender for high throughput** - Wrap slow appenders (file, network)
4. **Configure appropriate buffer sizes** - Balance memory usage vs. throughput
5. **Use separate appenders for different concerns** - e.g., errors.log, security.log
6. **Set appropriate log levels** - Avoid verbose logging to slow appenders
7. **Monitor appender health** - Check `isStarted()` in health checks

## Appender Configuration Examples

### Development Setup

```java
// Simple console logging for development
ConsoleAppender console = new ConsoleAppender();
console.setLayout(new PatternLayout("%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"));
console.start();
logger.addAppender(console);
```

### Production Setup

```java
// Async file logging with rolling for production
RollingFileAppender fileAppender = new RollingFileAppender(
    "logs/app.log",
    "logs/app-%d{yyyy-MM-dd}.log.gz"
);
fileAppender.setLayout(new PatternLayout("%d %-5level [%thread] %logger - %msg%n"));
fileAppender.setMaxFileSize("100MB");
fileAppender.setMaxHistory(30);

AsyncAppender asyncAppender = new AsyncAppender(fileAppender);
asyncAppender.setQueueSize(8192);
asyncAppender.start();

logger.addAppender(asyncAppender);
```

### Error Log Separation

```java
// Separate file for errors
FileAppender errorAppender = new FileAppender("errors.log");
errorAppender.setMinLevel(LogLevel.ERROR);
errorAppender.setLayout(new PatternLayout("%d %-5level %logger - %msg%n%exception"));
errorAppender.start();
logger.addAppender(errorAppender);
```
