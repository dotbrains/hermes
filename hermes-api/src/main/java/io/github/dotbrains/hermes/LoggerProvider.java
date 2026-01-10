package io.github.dotbrains.hermes;

/**
 * Service Provider Interface for logger implementations.
 * Implementations should be registered via Java's ServiceLoader mechanism.
 */
public interface LoggerProvider {
    
    /**
     * Gets a logger with the specified name.
     */
    Logger getLogger(String name);
}
