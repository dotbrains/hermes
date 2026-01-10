# Kotlin DSL

Hermes provides idiomatic Kotlin extensions for a more natural logging experience in Kotlin projects.

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-kotlin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.dotbrains:hermes-kotlin:1.0.0-SNAPSHOT")
}
```

## Requirements

- Kotlin 2.1.10+
- Java 17+

## Logger Creation

### Extension Property

```kotlin
import io.github.dotbrains.kotlin.logger

class UserService {
    private val log = UserService::class.logger

    fun createUser(username: String) {
        log.info { "Creating user: $username" }
    }
}
```

### Inline Logger

```kotlin
import io.github.dotbrains.kotlin.logger

class OrderService {
    fun processOrder(orderId: Long) {
        logger.info { "Processing order: $orderId" }
    }
}
```

## Lazy Evaluation

All logging methods accept lambda expressions for lazy evaluation:

```kotlin
// Message only evaluated if INFO level is enabled
log.info { "User details: ${user.toDetailedString()}" }

// Expensive computation avoided if DEBUG is disabled
log.debug { "Query result: ${complexQuery().formatResults()}" }
```

### Traditional vs. Lazy

```kotlin
// ❌ Bad - string interpolation happens regardless of log level
log.debug("Result: ${expensiveOperation()}")

// ✅ Good - only executed if DEBUG is enabled
log.debug { "Result: ${expensiveOperation()}" }
```

## MDC Extensions

### Scoped MDC

Automatic MDC cleanup with `withMDC`:

```kotlin
import io.github.dotbrains.kotlin.withMDC

fun processRequest(requestId: String, userId: String) {
    withMDC("requestId" to requestId, "userId" to userId) {
        log.info { "Processing request" }
        // MDC automatically cleared after block
    }
}
```

### MDC Builder

```kotlin
import io.github.dotbrains.kotlin.mdc

fun handleRequest(request: Request) {
    mdc {
        "requestId" to request.id
        "method" to request.method
        "path" to request.path
        "userId" to request.userId
    }.use {
        log.info { "Handling ${request.method} ${request.path}" }
    }
}
```

### Suspending Functions

MDC propagation in coroutines:

```kotlin
import io.github.dotbrains.kotlin.withMDC
import kotlinx.coroutines.launch

suspend fun processAsync(requestId: String) {
    withMDC("requestId" to requestId) {
        launch {
            log.info { "Async processing started" }
            // MDC available in coroutine
        }
    }
}
```

## Structured Logging

### Using `infoWith`, `debugWith`, etc.

```kotlin
log.infoWith {
    "message" to "User created"
    "userId" to userId
    "username" to username
    "timestamp" to System.currentTimeMillis()
}
```

Output (with JsonLayout):
```json
{
  "timestamp": "2024-01-10T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.example.UserService",
  "message": "User created",
  "fields": {
    "userId": "user-123",
    "username": "alice",
    "timestamp": 1704882645123
  }
}
```

### All Levels

```kotlin
log.traceWith { /* fields */ }
log.debugWith { /* fields */ }
log.infoWith { /* fields */ }
log.warnWith { /* fields */ }
log.errorWith { /* fields */ }
```

## Exception Logging

### Lambda with Exception

```kotlin
try {
    processPayment()
} catch (e: PaymentException) {
    log.error(e) { "Payment processing failed for order $orderId" }
}
```

### Structured Exception Logging

```kotlin
try {
    executeTransaction()
} catch (e: Exception) {
    log.errorWith(e) {
        "message" to "Transaction failed"
        "transactionId" to txId
        "amount" to amount
        "retryCount" to retries
    }
}
```

## Markers

### Creating Markers

```kotlin
import io.github.dotbrains.kotlin.marker

val SECURITY = marker("SECURITY")
val AUDIT = marker("AUDIT")
val PERFORMANCE = marker("PERFORMANCE")
```

### Using Markers

```kotlin
log.info(SECURITY) { "User $username logged in" }
log.warn(PERFORMANCE) { "Query took ${duration}ms" }

// With structured logging
log.warnWith(AUDIT) {
    "message" to "Configuration changed"
    "setting" to settingName
    "oldValue" to oldValue
    "newValue" to newValue
    "changedBy" to adminId
}
```

## Conditional Logging

### Check Log Level

```kotlin
if (log.isDebugEnabled) {
    val details = computeExpensiveDetails()
    log.debug { "Details: $details" }
}
```

### Extension Functions

```kotlin
log.debugIf(condition) { "Message" }
log.infoIf(user.isAdmin) { "Admin action performed" }
```

## Coroutines Support

### Structured Concurrency

```kotlin
import kotlinx.coroutines.*

suspend fun processItems(items: List<Item>) = coroutineScope {
    withMDC("batchId" to UUID.randomUUID().toString()) {
        items.map { item ->
            async {
                withMDC("itemId" to item.id) {
                    log.info { "Processing item ${item.id}" }
                    processItem(item)
                }
            }
        }.awaitAll()
    }
}
```

### Flow Logging

```kotlin
import kotlinx.coroutines.flow.*

fun observeOrders(): Flow<Order> = flow {
    log.info { "Starting order observation" }
    // ...
}.onEach { order ->
    log.debug { "Order received: ${order.id}" }
}.catch { e ->
    log.error(e) { "Order stream error" }
}
```

## Extension Functions

### Timing Operations

```kotlin
import io.github.dotbrains.kotlin.logTime

val result = log.logTime("Database query") {
    database.query(sql)
}
// Logs: Database query completed in 45ms
```

### Conditional Execution

```kotlin
import io.github.dotbrains.kotlin.ifDebug

log.ifDebug {
    // Only executed if DEBUG is enabled
    val diagnostics = generateDiagnostics()
    log.debug { "Diagnostics: $diagnostics" }
}
```

## Best Practices

### 1. Use Lazy Evaluation

```kotlin
// ✅ Good - lazy evaluation
log.debug { "Result: ${expensiveOp()}" }

// ❌ Bad - eager evaluation
log.debug("Result: ${expensiveOp()}")
```

### 2. Scope MDC Properly

```kotlin
// ✅ Good - automatic cleanup
withMDC("key" to "value") {
    log.info { "Message" }
}

// ❌ Bad - manual cleanup required
MDC.put("key", "value")
log.info { "Message" }
MDC.remove("key")
```

### 3. Use Structured Logging

```kotlin
// ✅ Good - structured data
log.infoWith {
    "event" to "user_created"
    "userId" to user.id
    "email" to user.email
}

// ❌ Bad - unstructured strings
log.info { "User created: ${user.id}, ${user.email}" }
```

### 4. Leverage Type Safety

```kotlin
// ✅ Good - type-safe field names
data class LogFields(
    val userId: String,
    val action: String,
    val timestamp: Long
)

fun logEvent(fields: LogFields) {
    log.infoWith {
        "userId" to fields.userId
        "action" to fields.action
        "timestamp" to fields.timestamp
    }
}
```

## Complete Example

```kotlin
import io.github.dotbrains.kotlin.*
import kotlinx.coroutines.*

class OrderService {
    private val log = OrderService::class.logger

    private val AUDIT = marker("AUDIT")
    private val PERFORMANCE = marker("PERFORMANCE")

    suspend fun processOrder(order: Order) {
        withMDC("orderId" to order.id, "customerId" to order.customerId) {
            log.info { "Processing order ${order.id}" }

            val duration = log.logTime("Order processing") {
                try {
                    validateOrder(order)
                    log.debug { "Order validated" }

                    reserveInventory(order)
                    log.debug { "Inventory reserved" }

                    processPayment(order)
                    log.infoWith(AUDIT) {
                        "event" to "payment_processed"
                        "orderId" to order.id
                        "amount" to order.total
                        "currency" to order.currency
                    }

                    shipOrder(order)
                    log.info { "Order shipped successfully" }

                } catch (e: ValidationException) {
                    log.warn(e) { "Order validation failed" }
                    throw e
                } catch (e: PaymentException) {
                    log.errorWith(e) {
                        "event" to "payment_failed"
                        "orderId" to order.id
                        "reason" to e.reason
                    }
                    throw e
                }
            }

            if (duration > 1000) {
                log.warn(PERFORMANCE) {
                    "Order processing took ${duration}ms (threshold: 1000ms)"
                }
            }
        }
    }

    private suspend fun validateOrder(order: Order) {
        log.ifDebug {
            val details = order.toDetailedString()
            log.debug { "Validating order: $details" }
        }
        // validation logic
    }

    private suspend fun reserveInventory(order: Order) = coroutineScope {
        order.items.map { item ->
            async {
                withMDC("itemId" to item.id) {
                    log.debug { "Reserving ${item.quantity}x ${item.name}" }
                    inventory.reserve(item)
                }
            }
        }.awaitAll()
    }

    private suspend fun processPayment(order: Order) {
        log.info { "Processing payment: ${order.total} ${order.currency}" }
        payment.process(order)
    }

    private suspend fun shipOrder(order: Order) {
        log.info { "Shipping order to ${order.shippingAddress}" }
        shipping.ship(order)
    }
}
```

## Migration from Java

### Before (Java)

```java
private static final Logger log = LoggerFactory.getLogger(UserService.class);

public void createUser(String username) {
    log.info("Creating user: {}", username);
    MDC.put("userId", userId);
    try {
        // ...
        log.debug("User details: " + user.toDetailedString());
    } finally {
        MDC.remove("userId");
    }
}
```

### After (Kotlin)

```kotlin
private val log = UserService::class.logger

fun createUser(username: String) {
    log.info { "Creating user: $username" }
    withMDC("userId" to userId) {
        // ...
        log.debug { "User details: ${user.toDetailedString()}" }
    }
}
```

Benefits:

- Concise logger creation
- Lazy evaluation by default
- Automatic MDC cleanup
- Idiomatic Kotlin syntax
