# Core API

Complete API reference for core Hermes interfaces and classes.

## Logger Interface

Main logging interface with level-specific methods.

### Package

```java
io.github.dotbrains.Logger
```

### Methods

#### Trace Level

```java
void trace(String message)
void trace(String message, Object arg)
void trace(String message, Object arg1, Object arg2)
void trace(String message, Object... args)
void trace(String message, Throwable throwable)
void trace(Marker marker, String message)
void trace(Marker marker, String message, Object... args)
void trace(Marker marker, String message, Throwable throwable)
boolean isTraceEnabled()
boolean isTraceEnabled(Marker marker)
```

#### Debug Level

```java
void debug(String message)
void debug(String message, Object arg)
void debug(String message, Object arg1, Object arg2)
void debug(String message, Object... args)
void debug(String message, Throwable throwable)
void debug(Marker marker, String message)
void debug(Marker marker, String message, Object... args)
void debug(Marker marker, String message, Throwable throwable)
boolean isDebugEnabled()
boolean isDebugEnabled(Marker marker)
```

#### Info Level

```java
void info(String message)
void info(String message, Object arg)
void info(String message, Object arg1, Object arg2)
void info(String message, Object... args)
void info(String message, Throwable throwable)
void info(Marker marker, String message)
void info(Marker marker, String message, Object... args)
void info(Marker marker, String message, Throwable throwable)
boolean isInfoEnabled()
boolean isInfoEnabled(Marker marker)
```

#### Warn Level

```java
void warn(String message)
void warn(String message, Object arg)
void warn(String message, Object arg1, Object arg2)
void warn(String message, Object... args)
void warn(String message, Throwable throwable)
void warn(Marker marker, String message)
void warn(Marker marker, String message, Object... args)
void warn(Marker marker, String message, Throwable throwable)
boolean isWarnEnabled()
boolean isWarnEnabled(Marker marker)
```

#### Error Level

```java
void error(String message)
void error(String message, Object arg)
void error(String message, Object arg1, Object arg2)
void error(String message, Object... args)
void error(String message, Throwable throwable)
void error(Marker marker, String message)
void error(Marker marker, String message, Object... args)
void error(Marker marker, String message, Throwable throwable)
boolean isErrorEnabled()
boolean isErrorEnabled(Marker marker)
```

#### Metadata

```java
String getName()
```

### Usage Examples

```java
Logger log = LoggerFactory.getLogger(MyClass.class);

// Simple message
log.info("Application started");

// Parameterized message
log.info("User {} logged in from {}", username, ipAddress);

// With exception
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed", e);
}

// Conditional logging
if (log.isDebugEnabled()) {
    log.debug("Expensive data: {}", computeExpensiveData());
}

// With marker
Marker securityMarker = MarkerFactory.getMarker("SECURITY");
log.warn(securityMarker, "Failed login attempt for user {}", username);
```

## LoggerFactory

Static factory for obtaining Logger instances.

### Package

```java
io.github.dotbrains.LoggerFactory
```

### Methods

```java
static Logger getLogger(String name)
static Logger getLogger(Class<?> clazz)
```

### Usage

```java
// By class
Logger log = LoggerFactory.getLogger(UserService.class);

// By name
Logger log = LoggerFactory.getLogger("com.example.MyLogger");
```

## LogLevel Enum

Enumeration of log levels.

### Package

```java
io.github.dotbrains.LogLevel
```

### Values

```java
TRACE  // Most detailed
DEBUG
INFO
WARN
ERROR  // Least detailed, most severe
```

### Methods

```java
int toInt()
boolean isGreaterOrEqual(LogLevel other)
```

### Usage

```java
LogLevel level = LogLevel.INFO;
if (level.isGreaterOrEqual(LogLevel.WARN)) {
    // Level is WARN or ERROR
}
```

## MDC (Mapped Diagnostic Context)

Thread-local key-value storage for contextual logging.

### Package

```java
io.github.dotbrains.MDC
```

### Methods

```java
static void put(String key, String value)
static String get(String key)
static void remove(String key)
static void clear()
static Map<String, String> getCopyOfContextMap()
static void setContextMap(Map<String, String> contextMap)
```

### Usage

```java
// Add context
MDC.put("requestId", "req-12345");
MDC.put("userId", "user-789");

try {
    log.info("Processing request");  // Includes MDC values
} finally {
    MDC.clear();  // Always clean up
}

// Copy for async
Map<String, String> context = MDC.getCopyOfContextMap();
CompletableFuture.runAsync(() -> {
    MDC.setContextMap(context);
    log.info("Async processing");
    MDC.clear();
});
```

## Marker Interface

Marker for categorizing log events.

### Package

```java
io.github.dotbrains.Marker
```

### Methods

```java
String getName()
void add(Marker reference)
boolean remove(Marker reference)
boolean hasChildren()
Iterator<Marker> iterator()
boolean contains(Marker other)
boolean contains(String name)
```

### Usage

```java
Marker parent = MarkerFactory.getMarker("SECURITY");
Marker child = MarkerFactory.getMarker("AUDIT");
child.add(parent);

log.warn(child, "Security audit event");

// Check hierarchy
if (child.contains(parent)) {
    // child inherits from parent
}
```

## MarkerFactory

Factory for obtaining Marker instances.

### Package

```java
io.github.dotbrains.MarkerFactory
```

### Methods

```java
static Marker getMarker(String name)
static boolean exists(String name)
static boolean detachMarker(String name)
```

### Usage

```java
Marker marker = MarkerFactory.getMarker("PERFORMANCE");

if (MarkerFactory.exists("PERFORMANCE")) {
    // Marker already created
}

// Remove from registry
MarkerFactory.detachMarker("PERFORMANCE");
```

## @InjectLogger Annotation

Annotation for compile-time logger injection.

### Package

```java
io.github.dotbrains.InjectLogger
```

### Target

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface InjectLogger {
}
```

### Usage

```java
@InjectLogger
public class UserService extends UserServiceHermesLogger {
    // protected Logger log is available
    
    public void createUser(String username) {
        log.info("Creating user: {}", username);
    }
}
```

### Generated Code

```java
public abstract class UserServiceHermesLogger {
    protected final Logger log = LoggerFactory.getLogger(UserService.class);
}
```

## LoggerProvider SPI

Service Provider Interface for custom logger implementations.

### Package

```java
io.github.dotbrains.spi.LoggerProvider
```

### Methods

```java
Logger getLogger(String name)
```

### Usage

Implement and register via ServiceLoader:

```java
public class CustomLoggerProvider implements LoggerProvider {
    @Override
    public Logger getLogger(String name) {
        return new CustomLogger(name);
    }
}
```

Register in `META-INF/services/io.github.dotbrains.spi.LoggerProvider`:

```
com.example.CustomLoggerProvider
```
