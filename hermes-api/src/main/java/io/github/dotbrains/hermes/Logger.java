package io.github.dotbrains.hermes;

import java.util.function.Supplier;

/**
 * Main logging interface for Hermes.
 * Provides methods for logging at various levels with support for parameterized messages.
 */
public interface Logger {

    /**
     * Gets the name of this logger.
     */
    String getName();

    /**
     * Checks if TRACE level is enabled for this logger.
     */
    boolean isTraceEnabled();

    /**
     * Checks if DEBUG level is enabled for this logger.
     */
    boolean isDebugEnabled();

    /**
     * Checks if INFO level is enabled for this logger.
     */
    boolean isInfoEnabled();

    /**
     * Checks if WARN level is enabled for this logger.
     */
    boolean isWarnEnabled();

    /**
     * Checks if ERROR level is enabled for this logger.
     */
    boolean isErrorEnabled();

    // TRACE methods
    void trace(String message);
    void trace(String message, Object arg);
    void trace(String message, Object arg1, Object arg2);
    void trace(String message, Object... args);
    void trace(String message, Throwable throwable);
    void trace(Supplier<String> messageSupplier);
    void trace(Marker marker, String message);
    void trace(Marker marker, String message, Object... args);

    // DEBUG methods
    void debug(String message);
    void debug(String message, Object arg);
    void debug(String message, Object arg1, Object arg2);
    void debug(String message, Object... args);
    void debug(String message, Throwable throwable);
    void debug(Supplier<String> messageSupplier);
    void debug(Marker marker, String message);
    void debug(Marker marker, String message, Object... args);

    // INFO methods
    void info(String message);
    void info(String message, Object arg);
    void info(String message, Object arg1, Object arg2);
    void info(String message, Object... args);
    void info(String message, Throwable throwable);
    void info(Supplier<String> messageSupplier);
    void info(Marker marker, String message);
    void info(Marker marker, String message, Object... args);

    // WARN methods
    void warn(String message);
    void warn(String message, Object arg);
    void warn(String message, Object arg1, Object arg2);
    void warn(String message, Object... args);
    void warn(String message, Throwable throwable);
    void warn(Supplier<String> messageSupplier);
    void warn(Marker marker, String message);
    void warn(Marker marker, String message, Object... args);

    // ERROR methods
    void error(String message);
    void error(String message, Object arg);
    void error(String message, Object arg1, Object arg2);
    void error(String message, Object... args);
    void error(String message, Throwable throwable);
    void error(Supplier<String> messageSupplier);
    void error(Marker marker, String message);
    void error(Marker marker, String message, Object... args);
}
