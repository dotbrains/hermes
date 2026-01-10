# Installation

This guide covers various ways to install and configure Hermes in your project.

## Requirements

- Java 17 or higher
- Maven 3.8+ or Gradle 7+

## Maven Setup

### Standard Setup

Add the following to your `pom.xml`:

```xml
<dependencies>
    <!-- Core API -->
    <dependency>
        <groupId>io.github.dotbrains</groupId>
        <artifactId>hermes-api</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Annotation processor (for @InjectLogger) -->
    <dependency>
        <groupId>io.github.dotbrains</groupId>
        <artifactId>hermes-processor</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>

    <!-- Core implementation -->
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
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
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

### Spring Boot Setup

For Spring Boot applications, use the starter:

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

The starter automatically includes the API, processor, and core modules.

### Kotlin Setup

For Kotlin projects, add the Kotlin DSL module:

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-kotlin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Gradle Setup

### Standard Setup

Add the following to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.dotbrains:hermes-api:1.0.0-SNAPSHOT'
    annotationProcessor 'io.github.dotbrains:hermes-processor:1.0.0-SNAPSHOT'
    runtimeOnly 'io.github.dotbrains:hermes-core:1.0.0-SNAPSHOT'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

### Kotlin DSL (build.gradle.kts)

```kotlin
dependencies {
    implementation("io.github.dotbrains:hermes-api:1.0.0-SNAPSHOT")
    annotationProcessor("io.github.dotbrains:hermes-processor:1.0.0-SNAPSHOT")
    runtimeOnly("io.github.dotbrains:hermes-core:1.0.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

### Spring Boot with Gradle

```gradle
dependencies {
    implementation 'io.github.dotbrains:hermes-spring-boot-starter:1.0.0-SNAPSHOT'
}
```

## Dependency Overview

Understanding the module structure:

| Module | Purpose | Scope |
|--------|---------|-------|
| `hermes-api` | Core interfaces and annotations | compile |
| `hermes-processor` | Annotation processor for `@InjectLogger` | provided |
| `hermes-core` | Implementation with appenders and layouts | runtime |
| `hermes-spring-boot-starter` | Spring Boot auto-configuration | compile |
| `hermes-kotlin` | Kotlin DSL extensions | compile |

!!! tip "Why different scopes?"
    - `hermes-api` is needed at compile time for your code
    - `hermes-processor` only runs during compilation
    - `hermes-core` is discovered at runtime via ServiceLoader

## Verifying Installation

Create a simple test class:

```java
import io.github.dotbrains.InjectLogger;

@InjectLogger
public class InstallationTest extends InstallationTestHermesLogger {
    
    public static void main(String[] args) {
        InstallationTest test = new InstallationTest();
        test.testLogging();
    }
    
    public void testLogging() {
        log.info("Hermes is installed correctly!");
    }
}
```

Build and run:

```bash
mvn clean compile exec:java -Dexec.mainClass="InstallationTest"
```

You should see:

```
[INFO] Hermes is installed correctly!
```

## Next Steps

- Follow the [Quick Start](quick-start.md) guide
- Learn about [Basic Usage](basic-usage.md)
- Configure [Spring Boot Integration](../integrations/spring-boot.md)
