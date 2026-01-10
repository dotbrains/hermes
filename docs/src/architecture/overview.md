# Architecture Overview

Hermes is designed as a high-performance, modular logging library with a focus on developer experience and zero-allocation optimization.

## Design Principles

1. **Performance First**: Zero-allocation message formatting, async logging with LMAX Disruptor
2. **Developer Experience**: Zero-boilerplate logger injection via compile-time annotation processing
3. **Modularity**: Clear separation between API, implementation, and integrations
4. **GraalVM Native**: Full support for AOT compilation without reflection
5. **Type Safety**: Compile-time checks, no runtime surprises

## High-Level Architecture

```mermaid
flowchart TB
    App["Application Code"]
    
    subgraph api["hermes-api"]
        direction TB
        Logger["Logger"]
        LoggerFactory["LoggerFactory"]
        LogLevel["LogLevel"]
        MDC["MDC"]
        Marker["Marker"]
        InjectLogger["@InjectLogger"]
    end
    
    subgraph core["hermes-core"]
        direction TB
        HermesLogger["HermesLogger"]
        LogEvent["LogEvent"]
        MessageFormatter["MessageFormatter"]
    end
    
    subgraph appenders["Appenders"]
        direction LR
        Console["Console"]
        File["File"]
        Rolling["Rolling"]
        Async["Async"]
        Logstash["Logstash"]
    end
    
    subgraph layouts["Layouts"]
        direction LR
        Pattern["Pattern"]
        JSON["JSON"]
    end
    
    App --> api
    api -->|"ServiceLoader<br/>Discovery"| core
    core --> appenders
    core --> layouts
    
    style api fill:#1976d2,stroke:#0d47a1,stroke-width:3px,color:#fff
    style core fill:#f57c00,stroke:#e65100,stroke-width:3px,color:#fff
    style appenders fill:#7b1fa2,stroke:#4a148c,stroke-width:3px,color:#fff
    style layouts fill:#388e3c,stroke:#1b5e20,stroke-width:3px,color:#fff
    
    style Logger fill:#1565c0,stroke:#0d47a1,color:#fff
    style LoggerFactory fill:#1565c0,stroke:#0d47a1,color:#fff
    style LogLevel fill:#1565c0,stroke:#0d47a1,color:#fff
    style MDC fill:#1565c0,stroke:#0d47a1,color:#fff
    style Marker fill:#1565c0,stroke:#0d47a1,color:#fff
    style InjectLogger fill:#1565c0,stroke:#0d47a1,color:#fff
    
    style HermesLogger fill:#e65100,stroke:#bf360c,color:#fff
    style LogEvent fill:#e65100,stroke:#bf360c,color:#fff
    style MessageFormatter fill:#e65100,stroke:#bf360c,color:#fff
    
    style Console fill:#6a1b9a,stroke:#4a148c,color:#fff
    style File fill:#6a1b9a,stroke:#4a148c,color:#fff
    style Rolling fill:#6a1b9a,stroke:#4a148c,color:#fff
    style Async fill:#6a1b9a,stroke:#4a148c,color:#fff
    style Logstash fill:#6a1b9a,stroke:#4a148c,color:#fff
    
    style Pattern fill:#2e7d32,stroke:#1b5e20,color:#fff
    style JSON fill:#2e7d32,stroke:#1b5e20,color:#fff
```

## Core Components

### API Layer (hermes-api)

**Purpose**: Define the public contract

- `Logger`: Main logging interface
- `LoggerFactory`: Logger instance creation
- `@InjectLogger`: Annotation for automatic logger injection
- `MDC`: Mapped Diagnostic Context
- `Marker`: Log event categorization
- `LogLevel`: Log level enumeration

**Design**: Pure interfaces with no implementation dependencies

### Implementation (hermes-core)

**Purpose**: High-performance logging engine

- `HermesLogger`: Concrete Logger implementation
- `LogEvent`: Immutable log event record
- `MessageFormatter`: Zero-allocation message formatting
- `Appender`: Output destination abstraction
- `Layout`: Log event formatting

**Optimizations**:

- Early exit (level checking before formatting)
- ThreadLocal StringBuilder (zero-allocation formatting)
- Immutable LogEvent (thread-safe for async)
- LMAX Disruptor (lock-free async logging)

### Annotation Processor (hermes-processor)

**Purpose**: Compile-time logger field generation

- Processes `@InjectLogger` annotations
- Generates base classes with `protected Logger log` field
- Runs during Maven/Gradle compilation
- Zero runtime overhead

### Spring Boot Starter (hermes-spring-boot-starter)

**Purpose**: Auto-configuration for Spring Boot

- `HermesAutoConfiguration`: Auto-configures logging
- `HermesProperties`: Binds to `hermes.*` properties
- `HermesLoggingHealthIndicator`: Health check integration

### Kotlin DSL (hermes-kotlin)

**Purpose**: Idiomatic Kotlin extensions

- Extension properties for logger creation
- Lazy evaluation with lambdas
- MDC scope functions
- Structured logging DSL

## Data Flow

### Synchronous Logging

```mermaid
flowchart TD
    A[log.info message, args] --> B{INFO enabled?}
    B -->|No| C[Early exit]
    B -->|Yes| D[Format message<br/>ThreadLocal StringBuilder]
    D --> E[Create immutable LogEvent]
    E --> F[Pass to all appenders]
    F --> G[Each appender applies layout]
    G --> H[Write output]
    
    style C fill:#ef5350,stroke:#c62828,color:#fff
    style E fill:#66bb6a,stroke:#2e7d32,color:#fff
```

### Asynchronous Logging

```mermaid
flowchart TD
    A[log.info message, args] --> B{INFO enabled?}
    B -->|No| C[Early exit]
    B -->|Yes| D[Format message]
    D --> E[Create LogEvent]
    E --> F[Publish to ring buffer<br/>non-blocking]
    F --> G[Calling thread continues]
    
    H[Background thread] --> I[Consume from ring buffer]
    I --> J[Pass to wrapped appenders]
    J --> K[Write output]
    
    F -.->|async| H
    
    style C fill:#ef5350,stroke:#c62828,color:#fff
    style F fill:#ffa726,stroke:#e65100,color:#000
    style I fill:#66bb6a,stroke:#2e7d32,color:#fff
```

## ServiceLoader Pattern

Hermes uses Java's ServiceLoader for provider discovery:

```mermaid
flowchart TD
    A[LoggerFactory.getLogger] --> B[ServiceLoader.load<br/>LoggerProvider.class]
    B --> C[Discover via<br/>META-INF/services]
    C --> D[HermesLoggerProvider]
    D --> E[HermesLogger instance]
    
    style C fill:#ab47bc,stroke:#6a1b9a,color:#fff
```

**Benefits**:

- Decouples API from implementation
- Supports custom implementations
- Works in GraalVM native images

## Thread Safety

### Thread-Local Components

- `MessageFormatter`: ThreadLocal StringBuilder per thread
- `MDC`: ThreadLocal map per thread

### Immutable Components

- `LogEvent`: Immutable record, safe to pass between threads
- `Logger`: Thread-safe singleton per class

### Concurrent Components

- `AsyncAppender`: Lock-free ring buffer (LMAX Disruptor)
- `Appender`: Must be thread-safe (multiple threads may log)

## Memory Management

### Zero-Allocation Path

1. Check log level (no allocation)
2. Retrieve ThreadLocal StringBuilder (reused)
3. Format message into StringBuilder (no new String)
4. Create LogEvent (single allocation)
5. Pass to appenders

**Result**: Only 1 allocation per enabled log statement

### Async Buffer

- Pre-allocated ring buffer of LogEvent slots
- Fixed memory footprint
- No GC pressure from logging

## Performance Characteristics

### Latency

- **Level check**: ~1-2ns
- **Disabled log statement**: ~2-5ns (early exit)
- **Enabled log statement (sync)**: ~50-100ns
- **Enabled log statement (async)**: ~500-1000ns (publish to ring buffer)

### Throughput

- **Synchronous**: ~1-2M messages/sec
- **Asynchronous**: ~10-15M messages/sec

### Memory

- **Logger instance**: ~100 bytes
- **LogEvent**: ~200 bytes
- **ThreadLocal StringBuilder**: ~2KB per thread
- **Async ring buffer**: (queue-size × 200 bytes)

## Extension Points

### Custom Appenders

Implement `Appender` interface:

```java
public interface Appender {
    void append(LogEvent event);
    void start();
    void stop();
    boolean isStarted();
    void setLayout(Layout layout);
}
```

### Custom Layouts

Implement `Layout` interface:

```java
public interface Layout {
    String format(LogEvent event);
}
```

### Custom Logger Provider

Implement `LoggerProvider` interface and register via ServiceLoader.

## Design Decisions

### Why Annotation Processing?

**Alternatives**: Lombok, AspectJ, runtime reflection

**Chosen**: Annotation processing

**Reasons**:

- Zero runtime overhead
- GraalVM native-image compatible
- IDE support (auto-completion)
- Compile-time errors

### Why LMAX Disruptor?

**Alternatives**: ArrayBlockingQueue, LinkedBlockingQueue

**Chosen**: LMAX Disruptor

**Reasons**:

- Lock-free (no contention)
- ~10x faster than blocking queues
- Mechanical sympathy (cache-friendly)
- Battle-tested (used by LMAX Exchange)

### Why ThreadLocal StringBuilder?

**Alternatives**: StringBuilder per call, String concatenation

**Chosen**: ThreadLocal StringBuilder

**Reasons**:

- Zero allocation (reused)
- Thread-safe (thread-local)
- Fast (no synchronization)

### Why Immutable LogEvent?

**Alternatives**: Mutable LogEvent, pooled events

**Chosen**: Immutable record

**Reasons**:

- Thread-safe for async
- Simple reasoning
- Compact memory layout (Java 17 records)

## Future Enhancements

Potential future additions:

1. **Filters**: Pre-appender filtering by level/marker/MDC
2. **Dynamic Configuration**: Runtime level changes without restart
3. **Metrics**: Built-in logging metrics (throughput, dropped events)
4. **Batching**: Batch writes for network appenders
5. **Compression**: Automatic log compression for file appenders
