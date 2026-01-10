# Hermes Examples

This module contains working examples demonstrating how to use Hermes logging library in real applications.

## Available Examples

### 1. Basic Logging Demo (`Demo.java`)

Demonstrates core Hermes logging features:
- Basic log levels (TRACE, DEBUG, INFO, WARN, ERROR)
- Parameterized logging with `{}` placeholders
- MDC (Mapped Diagnostic Context) for thread-local context
- Exception logging
- Conditional logging with `isDebugEnabled()` guards

**Run:**
```fish
mvn clean compile exec:java -Dexec.mainClass="io.github.dotbrains.hermes.examples.Demo" -pl hermes-examples
```

### 2. Annotation Processor Demo (`AnnotationProcessorDemo.java`)

Demonstrates zero-boilerplate logging using the `@InjectLogger` annotation:
- Automatic logger field injection at compile-time
- No manual `LoggerFactory.getLogger()` calls needed
- Base class generation via annotation processor
- GraalVM native-image compatible (no runtime reflection)

**Run:**
```fish
mvn clean compile exec:java -Dexec.mainClass="io.github.dotbrains.hermes.examples.AnnotationProcessorDemo" -pl hermes-examples
```

**View generated code:**
```fish
ls -la hermes-examples/target/generated-sources/annotations/io/github/dotbrains/hermes/examples/
cat hermes-examples/target/generated-sources/annotations/io/github/dotbrains/hermes/examples/AnnotationProcessorDemoHermesLogger.java
```

## Building the Examples

```fish
# Build all examples
mvn clean compile -pl hermes-examples -am

# Build and run a specific example
mvn clean compile exec:java -Dexec.mainClass="io.github.dotbrains.hermes.examples.Demo" -pl hermes-examples
```

## How the Annotation Processor Works

When you annotate a class with `@InjectLogger`:

```java
@InjectLogger
public class MyService extends MyServiceHermesLogger {
    // 'log' field is available automatically
    public void doWork() {
        log.info("Working...");
    }
}
```

The annotation processor generates a base class at compile-time:

```java
public abstract class MyServiceHermesLogger {
    protected final Logger log = LoggerFactory.getLogger(MyService.class);
}
```

This happens during Maven's compile phase, so there's no runtime overhead or reflection needed.

## Adding Your Own Examples

1. Create a new Java class in `src/main/java/io/github/dotbrains/hermes/examples/`
2. Add a `main()` method
3. Use Hermes logging features
4. Run with: `mvn exec:java -Dexec.mainClass="io.github.dotbrains.hermes.examples.YourClass" -pl hermes-examples`

## Dependencies

This module depends on:
- `hermes-api`: Core logging interfaces
- `hermes-core`: High-performance implementation
- `hermes-processor`: Annotation processor (compile-time only)

## Requirements

- Java 17 or higher
- Maven 3.8+
