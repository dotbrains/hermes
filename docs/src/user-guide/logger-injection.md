# Logger Injection

Hermes provides the `@InjectLogger` annotation for zero-boilerplate logger injection using compile-time annotation processing.

## Basic Usage

Annotate your class with `@InjectLogger` and extend the generated base class:

```java
import io.hermes.InjectLogger;

@InjectLogger
public class UserService extends UserServiceHermesLogger {
    
    public void createUser(String username) {
        log.info("Creating user: {}", username);
    }
}
```

## How It Works

The annotation processor generates a base class during compilation:

1. At compile time, the processor detects `@InjectLogger`
2. It generates `<ClassName>HermesLogger` with a protected `Logger log` field
3. Your class extends this base class and inherits the logger

### Generated Code

For the example above, the processor generates:

```java
public abstract class UserServiceHermesLogger {
    protected final Logger log = LoggerFactory.getLogger(UserService.class);
}
```

## Benefits

- **Zero runtime reflection**: Everything happens at compile time
- **GraalVM native-image compatible**: No reflection metadata required
- **Type-safe**: Compile-time errors if used incorrectly
- **Clean code**: No boilerplate logger instantiation

## Configuration

No configuration needed - the annotation processor runs automatically during Maven compilation via `maven-compiler-plugin`.

## Viewing Generated Sources

Generated sources are located in:
```
target/generated-sources/annotations/
```

## Alternative: Manual Logger Creation

If you prefer not to use annotation processing, create loggers manually:

```java
import io.hermes.Logger;
import io.hermes.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
}
```
