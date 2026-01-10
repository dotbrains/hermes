package io.github.dotbrains.hermes;

/**
 * Enum representing the various logging levels in Hermes.
 * Levels are ordered from least to most severe.
 */
public enum LogLevel {
    /**
     * Fine-grained informational events useful for debugging.
     */
    TRACE(0),
    
    /**
     * Informational messages that highlight the progress of the application.
     */
    DEBUG(1),
    
    /**
     * Informational messages highlighting normal application flow.
     */
    INFO(2),
    
    /**
     * Potentially harmful situations that deserve attention.
     */
    WARN(3),
    
    /**
     * Error events that might still allow the application to continue running.
     */
    ERROR(4);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }

    /**
     * Gets the severity level of this log level.
     * Higher numbers indicate more severe levels.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Checks if this level is enabled given a minimum level.
     */
    public boolean isEnabled(LogLevel minimumLevel) {
        return this.severity >= minimumLevel.severity;
    }
}
