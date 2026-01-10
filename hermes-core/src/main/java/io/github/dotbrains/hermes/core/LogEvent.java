package io.github.dotbrains.hermes.core;

import io.github.dotbrains.hermes.LogLevel;
import io.github.dotbrains.hermes.Marker;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable record representing a logging event.
 */
public record LogEvent(
    String loggerName,
    LogLevel level,
    String message,
    Object[] arguments,
    Throwable throwable,
    Marker marker,
    Instant timestamp,
    String threadName,
    long threadId,
    Map<String, String> mdcContext
) {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String loggerName;
        private LogLevel level;
        private String message;
        private Object[] arguments;
        private Throwable throwable;
        private Marker marker;
        private Instant timestamp = Instant.now();
        private String threadName = Thread.currentThread().getName();
        private long threadId = Thread.currentThread().getId();
        private Map<String, String> mdcContext;
        
        public Builder loggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }
        
        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder arguments(Object[] arguments) {
            this.arguments = arguments;
            return this;
        }
        
        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }
        
        public Builder marker(Marker marker) {
            this.marker = marker;
            return this;
        }
        
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }
        
        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }
        
        public Builder mdcContext(Map<String, String> mdcContext) {
            this.mdcContext = mdcContext;
            return this;
        }
        
        public LogEvent build() {
            return new LogEvent(
                loggerName,
                level,
                message,
                arguments,
                throwable,
                marker,
                timestamp,
                threadName,
                threadId,
                mdcContext
            );
        }
    }
}
