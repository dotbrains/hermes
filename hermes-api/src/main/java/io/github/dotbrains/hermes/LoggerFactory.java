package io.github.dotbrains.hermes;

import java.util.ServiceLoader;

/**
 * Factory for getting Logger instances.
 * Uses Java's ServiceLoader to find the logging implementation.
 */
public final class LoggerFactory {

    private static volatile LoggerProvider provider;
    private static final Object LOCK = new Object();

    private LoggerFactory() {
        // Utility class
    }

    /**
     * Gets a logger for the specified class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Gets a logger with the specified name.
     */
    public static Logger getLogger(String name) {
        return getProvider().getLogger(name);
    }

    /**
     * Gets the logger provider, loading it if necessary.
     */
    private static LoggerProvider getProvider() {
        if (provider == null) {
            synchronized (LOCK) {
                if (provider == null) {
                    provider = loadProvider();
                }
            }
        }
        return provider;
    }

    /**
     * Loads the logger provider using ServiceLoader.
     */
    private static LoggerProvider loadProvider() {
        ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class);

        for (LoggerProvider candidate : loader) {
            return candidate;
        }

        // Fallback to NOP logger if no provider found
        return new NopLoggerProvider();
    }

    /**
     * Manually sets the logger provider (useful for testing).
     */
    public static void setProvider(LoggerProvider newProvider) {
        synchronized (LOCK) {
            provider = newProvider;
        }
    }

    /**
     * Resets the provider (useful for testing).
     */
    public static void reset() {
        synchronized (LOCK) {
            provider = null;
        }
    }
}
