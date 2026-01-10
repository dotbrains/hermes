# Hermes ⚡

<img src="assets/og-image.svg" alt="Hermes Logo" width="600" />

![CI Build & Test](https://github.com/dotbrains/hermes/workflows/CI%20Build%20%26%20Test/badge.svg)
![Code Quality](https://github.com/dotbrains/hermes/workflows/Code%20Quality/badge.svg)
![Annotation Processor](https://github.com/dotbrains/hermes/workflows/Annotation%20Processor%20Validation/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.dotbrains/hermes-parent.svg?label=Maven%20Central)

![Java](https://img.shields.io/badge/-Java%2017-007396?style=flat-square&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Kotlin](https://img.shields.io/badge/-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![GraalVM](https://img.shields.io/badge/-GraalVM-232F3E?style=flat-square&logo=oracle&logoColor=white)
![LMAX Disruptor](https://img.shields.io/badge/-LMAX%20Disruptor-FF6B6B?style=flat-square&logo=apache&logoColor=white)

**High-performance logging library for Java with excellent developer experience**

Inspired by SLF4J, Hermes is a modern logging library that focuses on performance and developer productivity. Named after the Greek messenger god, Hermes delivers your log messages swiftly and reliably.

## Features

- 🚀 **Zero-boilerplate logging** - Use `@InjectLogger` annotation for automatic logger field injection
- ⚡ **High performance** - Async logging with LMAX Disruptor and zero-allocation optimization
- 🎯 **Fluent API** - Parameterized logging with `{}` placeholders
- ⚙️ **Easy configuration** - Configure via `application.yaml` with sensible defaults
- 🔧 **Spring Boot integration** - Auto-configuration support
- 🧵 **Thread-safe** - Built for concurrent applications
- 📝 **MDC support** - Mapped Diagnostic Context for contextual logging
- 🎨 **Markers** - Tag and categorize log messages
- 📁 **Multiple appenders** - Console, File, RollingFile, Async with Disruptor
- 📊 **JSON structured logging** - Built-in JSON layout for log aggregation
- 🎯 **Pattern layouts** - Customizable log output patterns

## Quick Example

```java
import io.github.dotbrains.InjectLogger;

@InjectLogger
public class UserService extends UserServiceHermesLogger {

    public void createUser(String username) {
        log.info("Creating user: {}", username);

        try {
            saveToDatabase(username);
            log.info("User {} created successfully", username);
        } catch (Exception e) {
            log.error("Failed to create user: {}", username, e);
        }
    }
}
```

## Why Hermes?

### Zero Boilerplate

No more typing `private static final Logger log = LoggerFactory.getLogger(ClassName.class);` in every class. Just add `@InjectLogger` and extend the generated base class.

### Performance First

- **Zero-allocation logging** with ThreadLocal StringBuilder
- **Async logging** powered by LMAX Disruptor for 10M+ logs/second
- **Early exit optimization** to avoid formatting when level is disabled

### Modern Java

Built for Java 17+, leveraging records, text blocks, and other modern features. Fully compatible with GraalVM native-image.

## Getting Started

Head over to the [Quick Start Guide](getting-started/quick-start.md) to begin using Hermes in your project.

## Architecture

Hermes follows a modular architecture:

- **hermes-api**: Core interfaces and annotations
- **hermes-core**: High-performance implementation
- **hermes-processor**: Annotation processor for `@InjectLogger`
- **hermes-spring-boot-starter**: Spring Boot auto-configuration
- **hermes-kotlin**: Kotlin DSL extensions
- **hermes-examples**: Example applications

Learn more in the [Architecture Overview](architecture/overview.md).

## Comparison with SLF4J

| Feature | SLF4J | Hermes |
|---------|-------|--------|
| Logger injection | Manual or Lombok | `@InjectLogger` annotation |
| Configuration | XML/Properties | YAML with sensible defaults |
| Async logging | Via Logback/Log4j | Built-in with Disruptor |
| Performance | Good | Optimized for zero-allocation |
| Spring Boot | External starters | Native integration |
| Java version | 8+ | 17+ (modern Java features) |

## License

MIT License - see LICENSE file for details
