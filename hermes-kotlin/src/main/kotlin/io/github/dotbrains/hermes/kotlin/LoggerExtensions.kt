package io.github.dotbrains.hermes.kotlin

import io.github.dotbrains.hermes.Logger
import io.github.dotbrains.hermes.LoggerFactory
import io.github.dotbrains.hermes.MDC

/**
 * Extension property to get a logger for any class.
 * Usage: val log = SomeClass::class.logger
 */
inline val <reified T : Any> kotlin.reflect.KClass<T>.logger: Logger
    get() = LoggerFactory.getLogger(this.java)

/**
 * Inline function to get logger for current class.
 * Usage: val log = logger()
 */
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

/**
 * DSL for MDC context that automatically clears after use.
 * Usage:
 * ```
 * withMDC("requestId" to "123", "userId" to "456") {
 *     log.info("Processing request")
 * }
 * ```
 */
inline fun <T> withMDC(vararg pairs: Pair<String, String>, block: () -> T): T {
    pairs.forEach { (key, value) -> MDC.put(key, value) }
    return try {
        block()
    } finally {
        MDC.clear()
    }
}

/**
 * Lazy logging extensions that only evaluate message if log level is enabled.
 */
inline fun Logger.trace(crossinline message: () -> String) {
    if (isTraceEnabled) {
        trace(message())
    }
}

inline fun Logger.debug(crossinline message: () -> String) {
    if (isDebugEnabled) {
        debug(message())
    }
}

inline fun Logger.info(crossinline message: () -> String) {
    if (isInfoEnabled) {
        info(message())
    }
}

inline fun Logger.warn(crossinline message: () -> String) {
    if (isWarnEnabled) {
        warn(message())
    }
}

inline fun Logger.error(crossinline message: () -> String) {
    if (isErrorEnabled) {
        error(message())
    }
}

/**
 * Extension for logging with exception and lazy message.
 */
inline fun Logger.error(throwable: Throwable, crossinline message: () -> String) {
    if (isErrorEnabled) {
        error(message(), throwable)
    }
}

/**
 * Structured logging DSL.
 * Usage:
 * ```
 * log.infoWith {
 *     "message" to "User created"
 *     "userId" to userId
 *     "username" to username
 * }
 * ```
 */
class StructuredLogBuilder {
    private val fields = mutableMapOf<String, Any?>()
    private var message: String = ""

    infix fun String.to(value: Any?) {
        if (this == "message") {
            message = value.toString()
        } else {
            fields[this] = value
        }
    }

    fun build(): String {
        val fieldsStr = fields.entries.joinToString(", ") { "${it.key}=${it.value}" }
        return if (fieldsStr.isEmpty()) message else "$message [$fieldsStr]"
    }
}

inline fun Logger.infoWith(block: StructuredLogBuilder.() -> Unit) {
    if (isInfoEnabled) {
        val builder = StructuredLogBuilder()
        builder.block()
        info(builder.build())
    }
}

inline fun Logger.warnWith(block: StructuredLogBuilder.() -> Unit) {
    if (isWarnEnabled) {
        val builder = StructuredLogBuilder()
        builder.block()
        warn(builder.build())
    }
}

inline fun Logger.errorWith(block: StructuredLogBuilder.() -> Unit) {
    if (isErrorEnabled) {
        val builder = StructuredLogBuilder()
        builder.block()
        error(builder.build())
    }
}
