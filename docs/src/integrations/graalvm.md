# GraalVM Native Image

Hermes is fully compatible with GraalVM Native Image, enabling AOT compilation to native executables with zero configuration.

## Overview

Hermes supports GraalVM native-image compilation out of the box:

- **No reflection at runtime**: Annotation processing happens at compile-time
- **Zero configuration**: Native-image metadata included in `hermes-core`
- **ServiceLoader support**: Logger provider discovery works in native images
- **Fast startup**: Sub-second application startup times
- **Low memory footprint**: Reduced memory usage compared to JVM

## Prerequisites

- GraalVM 21.0+  or Liberica NIK 22+
- Java 17+
- Maven 3.8+ or Gradle 7+

## Installation

### Install GraalVM

#### Using SDKMAN

```bash
sdk install java 21-graalce
sdk use java 21-graalce
```

#### Manual Download

Download from [GraalVM Downloads](https://www.graalvm.org/downloads/)

### Verify Installation

```bash
java -version
# Should show GraalVM

native-image --version
# Should show native-image version
```

## Maven Configuration

### Using Native Maven Plugin

```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>0.9.28</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <id>build-native</id>
                        <goals>
                            <goal>compile-no-fork</goal>
                        </goals>
                        <phase>package</phase>
                    </execution>
                </executions>
                <configuration>
                    <imageName>my-app</imageName>
                    <mainClass>com.example.Application</mainClass>
                    <buildArgs>
                        <buildArg>--no-fallback</buildArg>
                        <buildArg>-H:+ReportExceptionStackTraces</buildArg>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build Native Image

```bash
mvn clean package -Pnative
```

## Gradle Configuration

### Using Native Gradle Plugin

```kotlin
plugins {
    id("org.graalvm.buildtools.native") version "0.9.28"
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("my-app")
            mainClass.set("com.example.Application")
            buildArgs.add("--no-fallback")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
    }
}
```

### Build Native Image

```bash
./gradlew nativeCompile
```

## Native Image Metadata

Hermes includes native-image metadata in `hermes-core`:

### Location

```
hermes-core/src/main/resources/META-INF/native-image/io.github.dotbrains/hermes-core/
├── reflect-config.json
├── resource-config.json
├── jni-config.json
└── native-image.properties
```

### reflect-config.json

```json
[
  {
    "name": "io.github.dotbrains.core.HermesLoggerProvider",
    "methods": [
      {"name": "<init>", "parameterTypes": []}
    ]
  },
  {
    "name": "io.github.dotbrains.core.HermesLogger",
    "methods": [
      {"name": "<init>", "parameterTypes": ["java.lang.String"]}
    ]
  }
]
```

### resource-config.json

```json
{
  "resources": {
    "includes": [
      {"pattern": "META-INF/services/.*"},
      {"pattern": "hermes\\.properties"}
    ]
  }
}
```

## Spring Boot Native

### Maven Configuration

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <builder>paketobuildpacks/builder:tiny</builder>
                    <env>
                        <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
                    </env>
                </image>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Build Spring Boot Native Image

```bash
mvn spring-boot:build-image -Pnative
```

### Run Native Container

```bash
docker run -p 8080:8080 my-app:1.0.0-SNAPSHOT
```

## Logging Configuration

### application.properties

```properties
# Standard Hermes configuration works in native images
hermes.level.root=INFO
hermes.level.packages.com.example=DEBUG
hermes.pattern=%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n
hermes.async.enabled=true
hermes.async.queue-size=1024
```

### Appender Configuration

```java
@Configuration
public class NativeLoggingConfig {

    @Bean
    public ConsoleAppender consoleAppender() {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setLayout(new PatternLayout(
            "%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
        ));
        appender.start();
        return appender;
    }
}
```

## Performance Comparison

### Startup Time

| Configuration | JVM | Native Image |
|--------------|-----|--------------|
| Simple app | ~2.5s | ~0.05s |
| Spring Boot app | ~8s | ~0.15s |

### Memory Usage

| Configuration | JVM | Native Image |
|--------------|-----|--------------|
| Simple app | ~50MB | ~15MB |
| Spring Boot app | ~300MB | ~80MB |

### Throughput

- Native image throughput is comparable to JVM after warmup
- No JIT warmup period with native image
- Consistent performance from the start

## Troubleshooting

### ServiceLoader Issues

**Problem**: Logger provider not found

**Solution**: Verify `META-INF/services` files are included:

```bash
# Check if services file exists in JAR
jar tf target/my-app.jar | grep META-INF/services
```

### Reflection Errors

**Problem**: Class not found or method not accessible

**Solution**: Add to `reflect-config.json`:

```json
[
  {
    "name": "your.package.YourClass",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
  }
]
```

### Resource Loading

**Problem**: Configuration file not found

**Solution**: Add to `resource-config.json`:

```json
{
  "resources": {
    "includes": [
      {"pattern": "application\\.properties"},
      {"pattern": "application-.*\\.properties"}
    ]
  }
}
```

### Build Failures

**Problem**: Native image build fails

**Solution**: Enable verbose logging:

```bash
native-image \
  --no-fallback \
  -H:+ReportExceptionStackTraces \
  -H:+PrintClassInitialization \
  -jar target/my-app.jar
```

## Best Practices

### 1. Test in Native Mode Early

Build and test native images regularly during development:

```bash
mvn clean package -Pnative
./target/my-app
```

### 2. Minimize Reflection

Hermes avoids reflection by design:

- Annotation processing at compile-time
- ServiceLoader for provider discovery
- No runtime proxy generation

### 3. Profile Build Time

Monitor native image build times:

```bash
time mvn clean package -Pnative
```

Typical build times:

- Simple app: 1-2 minutes
- Spring Boot app: 3-5 minutes

### 4. Optimize Image Size

Reduce image size with build flags:

```xml
<buildArgs>
    <buildArg>--no-fallback</buildArg>
    <buildArg>-H:+StaticExecutableWithDynamicLibC</buildArg>
    <buildArg>--gc=G1</buildArg>
</buildArgs>
```

### 5. Use Async Logging

Async logging performs well in native images:

```yaml
hermes:
  async:
    enabled: true
    queue-size: 1024
```

## Complete Example

### Application Code

```java
import io.github.dotbrains.InjectLogger;

@InjectLogger
public class NativeApplication extends NativeApplicationHermesLogger {

    public static void main(String[] args) {
        NativeApplication app = new NativeApplication();
        app.run();
    }

    public void run() {
        log.info("Starting native application");

        long startTime = System.currentTimeMillis();
        performWork();
        long duration = System.currentTimeMillis() - startTime;

        log.info("Application completed in {}ms", duration);
    }

    private void performWork() {
        log.debug("Performing work...");
        // Application logic
    }
}
```

### pom.xml

```xml
<project>
    <properties>
        <hermes.version>1.0.0-SNAPSHOT</hermes.version>
        <native.maven.plugin.version>0.9.28</native.maven.plugin.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-api</artifactId>
            <version>${hermes.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-processor</artifactId>
            <version>${hermes.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>io.github.dotbrains</groupId>
            <artifactId>hermes-core</artifactId>
            <version>${hermes.version}</version>
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
                            <version>${hermes.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>${native.maven.plugin.version}</version>
                <extensions>true</extensions>
                <configuration>
                    <imageName>native-app</imageName>
                    <mainClass>com.example.NativeApplication</mainClass>
                    <buildArgs>
                        <buildArg>--no-fallback</buildArg>
                        <buildArg>-H:+ReportExceptionStackTraces</buildArg>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
# Build native image
mvn clean package -Pnative

# Run native executable
./target/native-app

# Output:
# 10:30:45.123 INFO  com.example.NativeApplication - Starting native application
# 10:30:45.125 DEBUG com.example.NativeApplication - Performing work...
# 10:30:45.150 INFO  com.example.NativeApplication - Application completed in 27ms
```

## Docker Example

### Dockerfile (Multi-stage build)

```dockerfile
# Build stage
FROM ghcr.io/graalvm/native-image:21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -Pnative -DskipTests

# Runtime stage
FROM ubuntu:22.04
WORKDIR /app
COPY --from=build /app/target/native-app .
EXPOSE 8080
ENTRYPOINT ["./native-app"]
```

### Build and Run

```bash
docker build -t native-app:latest .
docker run -p 8080:8080 native-app:latest
```

## Resources

- [GraalVM Documentation](https://www.graalvm.org/latest/docs/)
- [Native Image Build Configuration](https://www.graalvm.org/latest/reference-manual/native-image/overview/BuildConfiguration/)
- [Spring Boot GraalVM Native Support](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
