package io.github.dotbrains.hermes.core;

import io.github.dotbrains.hermes.LogLevel;
import io.github.dotbrains.hermes.Logger;
import io.github.dotbrains.hermes.MDC;
import io.github.dotbrains.hermes.Marker;
import io.github.dotbrains.hermes.core.appender.Appender;

import java.util.List;
import java.util.function.Supplier;

/**
 * Core implementation of the Logger interface.
 */
public class HermesLogger implements Logger {
    
    private final String name;
    private final List<Appender> appenders;
    private volatile LogLevel level;

    public HermesLogger(String name, LogLevel level, List<Appender> appenders) {
        this.name = name;
        this.level = level;
        this.appenders = appenders;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public LogLevel getLevel() {
        return level;
    }

    @Override
    public boolean isTraceEnabled() {
        return LogLevel.TRACE.isEnabled(level);
    }

    @Override
    public boolean isDebugEnabled() {
        return LogLevel.DEBUG.isEnabled(level);
    }

    @Override
    public boolean isInfoEnabled() {
        return LogLevel.INFO.isEnabled(level);
    }

    @Override
    public boolean isWarnEnabled() {
        return LogLevel.WARN.isEnabled(level);
    }

    @Override
    public boolean isErrorEnabled() {
        return LogLevel.ERROR.isEnabled(level);
    }

    // TRACE methods
    @Override
    public void trace(String message) {
        log(LogLevel.TRACE, null, message, null, null);
    }

    @Override
    public void trace(String message, Object arg) {
        log(LogLevel.TRACE, null, message, new Object[]{arg}, null);
    }

    @Override
    public void trace(String message, Object arg1, Object arg2) {
        log(LogLevel.TRACE, null, message, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void trace(String message, Object... args) {
        log(LogLevel.TRACE, null, message, args, MessageFormatter.extractThrowable(args));
    }

    @Override
    public void trace(String message, Throwable throwable) {
        log(LogLevel.TRACE, null, message, null, throwable);
    }

    @Override
    public void trace(Supplier<String> messageSupplier) {
        if (isTraceEnabled()) {
            log(LogLevel.TRACE, null, messageSupplier.get(), null, null);
        }
    }

    @Override
    public void trace(Marker marker, String message) {
        log(LogLevel.TRACE, marker, message, null, null);
    }

    @Override
    public void trace(Marker marker, String message, Object... args) {
        log(LogLevel.TRACE, marker, message, args, MessageFormatter.extractThrowable(args));
    }

    // DEBUG methods
    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, null, message, null, null);
    }

    @Override
    public void debug(String message, Object arg) {
        log(LogLevel.DEBUG, null, message, new Object[]{arg}, null);
    }

    @Override
    public void debug(String message, Object arg1, Object arg2) {
        log(LogLevel.DEBUG, null, message, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, null, message, args, MessageFormatter.extractThrowable(args));
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, null, message, null, throwable);
    }

    @Override
    public void debug(Supplier<String> messageSupplier) {
        if (isDebugEnabled()) {
            log(LogLevel.DEBUG, null, messageSupplier.get(), null, null);
        }
    }

    @Override
    public void debug(Marker marker, String message) {
        log(LogLevel.DEBUG, marker, message, null, null);
    }

    @Override
    public void debug(Marker marker, String message, Object... args) {
        log(LogLevel.DEBUG, marker, message, args, MessageFormatter.extractThrowable(args));
    }

    // INFO methods
    @Override
    public void info(String message) {
        log(LogLevel.INFO, null, message, null, null);
    }

    @Override
    public void info(String message, Object arg) {
        log(LogLevel.INFO, null, message, new Object[]{arg}, null);
    }

    @Override
    public void info(String message, Object arg1, Object arg2) {
        log(LogLevel.INFO, null, message, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void info(String message, Object... args) {
        log(LogLevel.INFO, null, message, args, MessageFormatter.extractThrowable(args));
    }

    @Override
    public void info(String message, Throwable throwable) {
        log(LogLevel.INFO, null, message, null, throwable);
    }

    @Override
    public void info(Supplier<String> messageSupplier) {
        if (isInfoEnabled()) {
            log(LogLevel.INFO, null, messageSupplier.get(), null, null);
        }
    }

    @Override
    public void info(Marker marker, String message) {
        log(LogLevel.INFO, marker, message, null, null);
    }

    @Override
    public void info(Marker marker, String message, Object... args) {
        log(LogLevel.INFO, marker, message, args, MessageFormatter.extractThrowable(args));
    }

    // WARN methods
    @Override
    public void warn(String message) {
        log(LogLevel.WARN, null, message, null, null);
    }

    @Override
    public void warn(String message, Object arg) {
        log(LogLevel.WARN, null, message, new Object[]{arg}, null);
    }

    @Override
    public void warn(String message, Object arg1, Object arg2) {
        log(LogLevel.WARN, null, message, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void warn(String message, Object... args) {
        log(LogLevel.WARN, null, message, args, MessageFormatter.extractThrowable(args));
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, null, message, null, throwable);
    }

    @Override
    public void warn(Supplier<String> messageSupplier) {
        if (isWarnEnabled()) {
            log(LogLevel.WARN, null, messageSupplier.get(), null, null);
        }
    }

    @Override
    public void warn(Marker marker, String message) {
        log(LogLevel.WARN, marker, message, null, null);
    }

    @Override
    public void warn(Marker marker, String message, Object... args) {
        log(LogLevel.WARN, marker, message, args, MessageFormatter.extractThrowable(args));
    }

    // ERROR methods
    @Override
    public void error(String message) {
        log(LogLevel.ERROR, null, message, null, null);
    }

    @Override
    public void error(String message, Object arg) {
        log(LogLevel.ERROR, null, message, new Object[]{arg}, null);
    }

    @Override
    public void error(String message, Object arg1, Object arg2) {
        log(LogLevel.ERROR, null, message, new Object[]{arg1, arg2}, null);
    }

    @Override
    public void error(String message, Object... args) {
        log(LogLevel.ERROR, null, message, args, MessageFormatter.extractThrowable(args));
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, null, message, null, throwable);
    }

    @Override
    public void error(Supplier<String> messageSupplier) {
        if (isErrorEnabled()) {
            log(LogLevel.ERROR, null, messageSupplier.get(), null, null);
        }
    }

    @Override
    public void error(Marker marker, String message) {
        log(LogLevel.ERROR, marker, message, null, null);
    }

    @Override
    public void error(Marker marker, String message, Object... args) {
        log(LogLevel.ERROR, marker, message, args, MessageFormatter.extractThrowable(args));
    }

    /**
     * Core logging method.
     */
    private void log(LogLevel logLevel, Marker marker, String message, Object[] args, Throwable throwable) {
        if (!logLevel.isEnabled(level)) {
            return;
        }

        LogEvent event = LogEvent.builder()
            .loggerName(name)
            .level(logLevel)
            .message(message)
            .arguments(args)
            .throwable(throwable)
            .marker(marker)
            .mdcContext(MDC.getCopyOfContextMap())
            .build();

        for (Appender appender : appenders) {
            if (appender.isStarted()) {
                appender.append(event);
            }
        }
    }
}
