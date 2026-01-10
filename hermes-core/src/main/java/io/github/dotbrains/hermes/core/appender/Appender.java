package io.github.dotbrains.hermes.core.appender;

import io.github.dotbrains.hermes.core.LogEvent;

/**
 * Interface for log appenders that write log events to various destinations.
 */
public interface Appender {
    
    /**
     * Appends a log event.
     */
    void append(LogEvent event);
    
    /**
     * Gets the name of this appender.
     */
    String getName();
    
    /**
     * Starts the appender.
     */
    void start();
    
    /**
     * Stops the appender and releases resources.
     */
    void stop();
    
    /**
     * Checks if this appender is started.
     */
    boolean isStarted();
}
