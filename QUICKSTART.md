# Hermes Quick Start Guide

Get up and running with Hermes in 5 minutes!

## Step 1: Add Dependencies

### Maven
Add to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.dotbrains</groupId>
        <artifactId>hermes-api</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>io.github.dotbrains</groupId>
        <artifactId>hermes-processor</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>io.github.dotbrains</groupId>
        <artifactId>hermes-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.github.dotbrains</groupId>
                        <artifactId>hermes-processor</artifactId>
                        <version>1.0.0-SNAPSHOT</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle

Add to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.dotbrains:hermes-api:1.0.0-SNAPSHOT'
    annotationProcessor 'io.github.dotbrains:hermes-processor:1.0.0-SNAPSHOT'
    runtimeOnly 'io.github.dotbrains:hermes-core:1.0.0-SNAPSHOT'
}
```

## Step 2: Create Your First Logged Class

Create a new Java class with the `@InjectLogger` annotation:

```java
package com.example.myapp;

import io.github.dotbrains.InjectLogger;

@InjectLogger
public class HelloHermes extends HelloHermesHermesLogger {
    
    public void sayHello(String name) {
        log.info("Hello, {}! Welcome to Hermes logging.", name);
    }
    
    public static void main(String[] args) {
        HelloHermes hello = new HelloHermes();
        hello.sayHello("World");
    }
}
```

**Important:** Your class must extend the generated base class `<ClassName>HermesLogger`.

## Step 3: Compile and Run

```bash
# Maven
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.myapp.HelloHermes"

# Gradle
./gradlew clean build
./gradlew run
```

## Step 4 (Optional): Configure Logging with Spring Boot

If you're using Spring Boot, add the starter dependency:

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Then create `src/main/resources/application.yml`:

```yaml
hermes:
  level:
    root: INFO
    packages:
      com.example: DEBUG
  pattern: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  async:
    enabled: false
    queue-size: 1024
```

## Alternative: Manual Logger Creation

If you prefer not to use annotation processing:

```java
package com.example.myapp;

import io.github.dotbrains.Logger;
import io.github.dotbrains.LoggerFactory;

public class HelloHermes {
    private static final Logger log = LoggerFactory.getLogger(HelloHermes.class);
    
    public void sayHello(String name) {
        log.info("Hello, {}! Welcome to Hermes logging.", name);
    }
    
    public static void main(String[] args) {
        HelloHermes hello = new HelloHermes();
        hello.sayHello("World");
    }
}
```

## Common Usage Patterns

### Parameterized Logging
```java
log.info("User {} logged in at {}", username, timestamp);
```

### Exception Logging
```java
try {
    dangerousOperation();
} catch (Exception e) {
    log.error("Operation failed: {}", operation, e);
}
```

### Conditional Logging
```java
if (log.isDebugEnabled()) {
    log.debug("Expensive operation result: {}", computeExpensive());
}
```

### Lazy Evaluation
```java
log.debug(() -> "Result: " + expensiveComputation());
```

### MDC (Mapped Diagnostic Context)
```java
import io.github.dotbrains.MDC;

MDC.put("userId", userId);
MDC.put("requestId", requestId);

try {
    processRequest();  // All logs will include userId and requestId
} finally {
    MDC.clear();
}
```

## Next Steps

- Read the full [README](README.md) for advanced features
- Check out [examples](hermes-examples/) for more use cases
- Configure log levels and patterns in `hermes.yaml`
- Add Spring Boot integration with `hermes-spring-boot-starter`

## Troubleshooting

### "Cannot resolve symbol log"
Make sure your class extends the generated `<ClassName>HermesLogger` base class.

### "No LoggerProvider found"
Add `hermes-core` as a runtime dependency.

### Annotation processor not running
Check that `hermes-processor` is configured in `annotationProcessorPaths`.

## Getting Help

- 📖 [Full Documentation](README.md)
- 💡 [Examples](hermes-examples/)
- 🐛 [Report Issues](https://github.com/dotbrains/hermes/issues)
