# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Hermes is a high-performance logging library for Java 17+ inspired by SLF4J. It provides zero-boilerplate logging through compile-time annotation processing, async logging with LMAX Disruptor, and comprehensive Spring Boot integration.

## Build & Development Commands

### Building the Project
```fish
# Clean build all modules
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build a specific module
mvn clean install -pl hermes-core -am
```

### Running Tests
```fish
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl hermes-api

# Run a single test class
mvn test -Dtest=MessageFormatterTest -pl hermes-core
```

### Compilation & Annotation Processing
```fish
# Compile with annotation processing enabled
mvn clean compile

# View generated source files (annotation processor output)
ls -la target/generated-sources/annotations/
```

### Maven Module Operations
```fish
# List all modules
mvn help:evaluate -Dexpression=project.modules

# Check dependency tree
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates
```

## Module Architecture

The project is split into six Maven modules with clear separation of concerns:

### hermes-api
- **Purpose**: Core public interfaces and annotations (the "contract")
- **Key files**: `Logger`, `LoggerFactory`, `@InjectLogger`, `MDC`, `Marker`, `LogLevel`
- **Dependencies**: None (pure API)
- **Design pattern**: Uses ServiceLoader to discover implementations at runtime

### hermes-core
- **Purpose**: High-performance implementation of the logging engine
- **Key components**:
  - `HermesLogger`: Concrete Logger implementation
  - `LogEvent`: Immutable log event record (Java 17 record)
  - `MessageFormatter`: Zero-allocation message formatting using ThreadLocal StringBuilder
  - **Appenders**: `ConsoleAppender`, `FileAppender`, `RollingFileAppender`, `AsyncAppender`, `LogstashAppender`
  - **Layouts**: `PatternLayout`, `JsonLayout`
- **Performance features**:
  - Early exit optimization (checks log level before formatting)
  - ThreadLocal StringBuilder for zero-allocation formatting
  - LMAX Disruptor for async logging (lock-free ring buffer)

### hermes-processor
- **Purpose**: Compile-time annotation processor for @InjectLogger
- **How it works**: Generates a base class `<ClassName>HermesLogger` with a `protected Logger log` field
- **Processing**: Runs during Maven's compile phase via `maven-compiler-plugin`

### hermes-spring-boot-starter
- **Purpose**: Spring Boot auto-configuration
- **Configuration**: Binds to `application.yml` under `hermes.*` properties
- **Components**: `HermesAutoConfiguration`, `HermesProperties`, `HermesLoggingHealthIndicator`

### hermes-kotlin
- **Purpose**: Idiomatic Kotlin DSL extensions
- **Features**: Extension properties, lazy evaluation, MDC DSL, structured logging builders
- **Requires**: Java 17+ (uses Kotlin 2.1.10)

### hermes-examples
- **Purpose**: Working examples and demonstrations
- **Use case**: Reference implementations for common scenarios

## Key Design Patterns

### ServiceLoader Pattern (SPI)
The API uses Java's ServiceLoader to discover the LoggerProvider implementation at runtime. This decouples the API from implementation:
- `LoggerFactory.getLogger()` → ServiceLoader → `HermesLoggerProvider` → `HermesLogger`
- Provider discovery happens once at startup
- Allows users to provide custom implementations

### Annotation Processing
The `@InjectLogger` annotation triggers compile-time code generation:
1. Developer writes: `@InjectLogger public class UserService extends UserServiceHermesLogger`
2. Processor generates: `UserServiceHermesLogger` with protected Logger field
3. Advantage: No runtime reflection, GraalVM native-image compatible

### Async Logging with LMAX Disruptor
AsyncAppender uses lock-free ring buffer for high throughput:
- Calling thread publishes to ring buffer (non-blocking)
- Background thread consumes and writes to appenders
- Trade-off: Small risk of log loss on crash before flush

### Immutability via Records
LogEvent is a Java 17 record, ensuring thread-safety for async processing:
- Immutable by design
- Compact memory layout
- Safe to pass between threads

## Configuration

### Spring Boot Configuration (application.yml)
```yaml
hermes:
  level:
    root: INFO
    packages:
      io.github.dotbrains: DEBUG
      com.example: TRACE
  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  async:
    enabled: true
    queue-size: 1024
```

### Programmatic Configuration
Appenders and layouts are configured programmatically in application code. See README.md "Advanced Features" section for examples.

## Common Development Patterns

### Adding a New Appender
1. Create class implementing `io.github.dotbrains.core.appender.Appender` interface
2. Implement `append(LogEvent)`, `start()`, `stop()`, `isStarted()` methods
3. Add lifecycle management (start/stop) in constructor
4. Location: `hermes-core/src/main/java/io/hermes/core/appender/`

### Adding a New Layout
1. Create class implementing `io.github.dotbrains.core.layout.Layout` interface
2. Implement `format(LogEvent)` method to return formatted string
3. Location: `hermes-core/src/main/java/io/hermes/core/layout/`

### Modifying Log Levels
Log levels are defined in `hermes-api/src/main/java/io/hermes/LogLevel.java` enum. Changes here affect the entire API contract.

### Working with MDC
MDC is thread-local storage managed in `hermes-api/src/main/java/io/hermes/MDC.java`. Changes must maintain thread-safety.

## Testing

### Test Framework
- Uses JUnit 5 (Jupiter)
- Test scope dependency defined in parent POM
- Currently no tests exist (test files will be in `src/test/java/`)

### Testing Annotation Processor
To test annotation processor changes:
1. Modify processor in `hermes-processor/`
2. Run `mvn clean compile -pl hermes-processor`
3. Create test class with `@InjectLogger` in examples module
4. Verify generated files in `target/generated-sources/annotations/`

## Dependencies

### Key External Dependencies
- **SnakeYAML 2.2**: YAML configuration parsing
- **LMAX Disruptor 4.0.0**: Lock-free async logging
- **Spring Boot 3.2.1**: Auto-configuration (optional)
- **Kotlin 2.1.10**: Kotlin DSL module (optional)

### Dependency Management
- All versions managed in parent `pom.xml` `<dependencyManagement>` section
- Disruptor is marked `<optional>true</optional>` in hermes-core

## Performance Considerations

### Zero-Allocation Optimizations
- ThreadLocal StringBuilder for message formatting (avoid creating new strings)
- Early exit when log level is disabled (avoid formatting entirely)
- Immutable LogEvent record (efficient memory layout)

### Async Logging
- Use AsyncAppender for high-throughput scenarios (10M+ logs/sec capability)
- Configure ring buffer size via `queue-size` (default 1024)
- Background thread processes events in batches

### Checking Log Levels
Always check log level before expensive operations:
```java
if (log.isDebugEnabled()) {
    log.debug("Result: {}", expensiveComputation());
}
```

## GraalVM Native Image

- Native-image metadata included in `hermes-core` under `META-INF/native-image/`
- Annotation processor is compile-time only (no reflection needed)
- Supports native compilation out of the box

## Important Implementation Notes

### Message Formatting
- Uses `{}` placeholder syntax (SLF4J-style)
- Exception must always be the last parameter
- Implementation in `MessageFormatter.java` uses ThreadLocal StringBuilder

### LogEvent Lifecycle
1. Logger checks if level is enabled
2. If enabled: format message, capture MDC context, create immutable LogEvent
3. Pass LogEvent to all configured appenders
4. Each appender applies its layout and writes output

### Spring Boot Integration
- Auto-configuration triggers when `hermes-spring-boot-starter` is on classpath
- Properties bind to `HermesProperties` class via `@ConfigurationProperties`
- Health indicator exposes logging status to Spring Boot Actuator

## File Locations Reference

- API interfaces: `hermes-api/src/main/java/io/hermes/`
- Core implementation: `hermes-core/src/main/java/io/hermes/core/`
- Appenders: `hermes-core/src/main/java/io/hermes/core/appender/`
- Layouts: `hermes-core/src/main/java/io/hermes/core/layout/`
- Annotation processor: `hermes-processor/src/main/java/io/hermes/processor/`
- Spring Boot: `hermes-spring-boot-starter/src/main/java/io/hermes/spring/`
- Kotlin DSL: `hermes-kotlin/src/main/kotlin/io/hermes/kotlin/`

## Requirements

- Java 17 or higher (uses records, text blocks, modern JVM features)
- Maven 3.8+ for building
- Fish shell environment (user preference noted)
