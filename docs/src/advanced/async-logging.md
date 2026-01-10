# Async Logging

Async logging provides high-throughput, non-blocking log processing using LMAX Disruptor.

## Overview

Hermes `AsyncAppender` uses a lock-free ring buffer to decouple log production from output:

1. **Calling thread**: Publishes LogEvent to ring buffer (non-blocking)
2. **Background thread**: Consumes events and writes to wrapped appender
3. **Trade-off**: Small risk of log loss on crash before flush

## Basic Setup

```java
import io.hermes.core.appender.AsyncAppender;
import io.hermes.core.appender.FileAppender;

// Create the target appender
FileAppender fileAppender = new FileAppender("app.log");
fileAppender.setLayout(new PatternLayout("%d %-5level %msg%n"));

// Wrap with AsyncAppender
AsyncAppender asyncAppender = new AsyncAppender(fileAppender);
asyncAppender.setQueueSize(8192);  // Ring buffer size (must be power of 2)
asyncAppender.start();

logger.addAppender(asyncAppender);
```

## Configuration

### Queue Size

The ring buffer size (must be a power of 2):

```java
asyncAppender.setQueueSize(1024);   // Small: 1K events
asyncAppender.setQueueSize(8192);   // Medium: 8K events
asyncAppender.setQueueSize(65536);  // Large: 64K events
```

**Considerations**:

- Larger = more memory, better handling of bursts
- Smaller = less memory, risk of blocking/dropping under load
- Default: 8192 (good balance)

### Blocking Behavior

Control what happens when the ring buffer is full:

```java
// Block until space available (ensures no log loss)
asyncAppender.setBlockWhenFull(true);

// Drop new events when full (prevents blocking)
asyncAppender.setBlockWhenFull(false);  // Default
```

### Timeout

Maximum time to wait when blocking:

```java
asyncAppender.setTimeout(1000);  // 1 second timeout
```

## LMAX Disruptor

Hermes uses the LMAX Disruptor for lock-free async processing.

### How It Works

1. **Ring Buffer**: Pre-allocated circular array of LogEvent slots
2. **Single Producer**: Calling thread publishes to next slot
3. **Single Consumer**: Background thread processes events in order
4. **Lock-Free**: Uses CAS operations and memory barriers
5. **Mechanical Sympathy**: Cache-line padding prevents false sharing

### Performance Characteristics

- **Throughput**: 10M+ messages/sec on modern hardware
- **Latency**: Sub-microsecond publish time
- **Allocation**: Zero allocation after startup
- **CPU**: One dedicated consumer thread

## Spring Boot Integration

Configure async logging in `application.yml`:

```yaml
hermes:
  async:
    enabled: true
    queue-size: 8192
    block-when-full: false
    timeout: 1000
```

Auto-configuration automatically wraps appenders:

```java
@Configuration
public class LoggingConfig {

    @Bean
    public AsyncAppender asyncAppender(HermesProperties properties) {
        FileAppender fileAppender = new FileAppender("app.log");

        AsyncAppender async = new AsyncAppender(fileAppender);
        async.setQueueSize(properties.getAsync().getQueueSize());
        async.setBlockWhenFull(properties.getAsync().isBlockWhenFull());
        async.start();

        return async;
    }
}
```

## Performance Comparison

### Synchronous Logging

```
Throughput: ~100K messages/sec
Latency: ~10-100µs per log statement
Blocking: Yes
```

### Asynchronous Logging

```
Throughput: ~10M messages/sec
Latency: ~0.1-1µs per log statement
Blocking: No (unless queue full and blocking enabled)
```

## Best Practices

### 1. Always Wrap Slow Appenders

Wrap file, network, or database appenders:

```java
// ✅ Good - file I/O on background thread
AsyncAppender async = new AsyncAppender(new FileAppender("app.log"));

// ❌ Bad - synchronous file I/O on calling thread
FileAppender sync = new FileAppender("app.log");
```

### 2. Choose Appropriate Queue Size

```java
// Low volume (<1K logs/sec)
asyncAppender.setQueueSize(1024);

// Medium volume (1K-10K logs/sec)
asyncAppender.setQueueSize(8192);

// High volume (>10K logs/sec)
asyncAppender.setQueueSize(65536);
```

### 3. Handle Shutdown Gracefully

Ensure logs are flushed before shutdown:

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    asyncAppender.stop();  // Flushes queue and stops consumer
}));
```

### 4. Monitor Queue Usage

Check for dropped events:

```java
long droppedEvents = asyncAppender.getDroppedEventCount();
if (droppedEvents > 0) {
    // Increase queue size or reduce log volume
}
```

### 5. Avoid Blocking Mode in Latency-Sensitive Code

```java
// ✅ Good for request processing
asyncAppender.setBlockWhenFull(false);

// ❌ Bad - can block requests
asyncAppender.setBlockWhenFull(true);
```

## Advanced Configuration

### Custom Wait Strategy

Control CPU vs. latency trade-off:

```java
// Busy-spin (lowest latency, high CPU)
asyncAppender.setWaitStrategy(WaitStrategy.BUSY_SPIN);

// Yielding (balanced)
asyncAppender.setWaitStrategy(WaitStrategy.YIELDING);

// Blocking (low CPU, higher latency)
asyncAppender.setWaitStrategy(WaitStrategy.BLOCKING);  // Default
```

### Multiple Async Appenders

Route different log levels to different async appenders:

```java
// Info logs to standard file
FileAppender infoFile = new FileAppender("info.log");
infoFile.setMaxLevel(LogLevel.INFO);
AsyncAppender asyncInfo = new AsyncAppender(infoFile);
asyncInfo.start();

// Error logs to separate file
FileAppender errorFile = new FileAppender("errors.log");
errorFile.setMinLevel(LogLevel.ERROR);
AsyncAppender asyncError = new AsyncAppender(errorFile);
asyncError.start();

logger.addAppender(asyncInfo);
logger.addAppender(asyncError);
```

## Troubleshooting

### Logs Not Appearing

**Issue**: Logs missing after application crash

**Solution**: Async appender buffer not flushed

```java
// Ensure shutdown hook flushes
asyncAppender.stop();
```

### High Memory Usage

**Issue**: Large queue size consuming memory

**Solution**: Reduce queue size or increase flush frequency

```java
asyncAppender.setQueueSize(1024);  // Reduce from 8192
```

### Dropped Events

**Issue**: `getDroppedEventCount()` > 0

**Solution**: Increase queue size or enable blocking

```java
asyncAppender.setQueueSize(65536);  // Increase capacity
// OR
asyncAppender.setBlockWhenFull(true);  // Prevent drops
```

### High CPU Usage

**Issue**: Background thread consuming CPU

**Solution**: Change wait strategy

```java
asyncAppender.setWaitStrategy(WaitStrategy.BLOCKING);
```

## Use Cases

### Web Applications

High request throughput requires non-blocking logging:

```java
AsyncAppender async = new AsyncAppender(new FileAppender("app.log"));
async.setQueueSize(8192);
async.setBlockWhenFull(false);  // Don't block requests
async.start();
```

### Batch Processing

High volume logging in background jobs:

```java
AsyncAppender async = new AsyncAppender(new FileAppender("batch.log"));
async.setQueueSize(65536);  // Large buffer for bursts
async.setBlockWhenFull(true);  // Ensure no loss
async.start();
```

### Microservices

Low-latency logging for high-throughput services:

```java
AsyncAppender async = new AsyncAppender(new LogstashAppender("localhost", 5000));
async.setQueueSize(8192);
async.setWaitStrategy(WaitStrategy.YIELDING);  // Balance latency/CPU
async.start();
```

## Async Logging Decision Matrix

| Scenario | Queue Size | Block When Full | Wait Strategy |
|----------|------------|-----------------|---------------|
| Web app (low volume) | 1024 | false | BLOCKING |
| Web app (high volume) | 8192 | false | YIELDING |
| Batch processing | 65536 | true | BLOCKING |
| Real-time system | 8192 | false | BUSY_SPIN |
| Background service | 8192 | true | BLOCKING |
