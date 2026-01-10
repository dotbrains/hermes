package io.github.dotbrains.hermes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapped Diagnostic Context (MDC) provides a way to enrich log messages
 * with contextual information stored in a thread-local map.
 */
public final class MDC {

    private static final ThreadLocal<Map<String, String>> context = 
        ThreadLocal.withInitial(HashMap::new);

    private MDC() {
        // Utility class
    }

    /**
     * Puts a key-value pair into the MDC context.
     */
    public static void put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        context.get().put(key, value);
    }

    /**
     * Gets a value from the MDC context.
     */
    public static String get(String key) {
        return context.get().get(key);
    }

    /**
     * Removes a key from the MDC context.
     */
    public static void remove(String key) {
        context.get().remove(key);
    }

    /**
     * Clears all entries from the MDC context.
     */
    public static void clear() {
        context.get().clear();
    }

    /**
     * Gets a copy of the current MDC context.
     */
    public static Map<String, String> getCopyOfContextMap() {
        return new HashMap<>(context.get());
    }

    /**
     * Sets the MDC context from a map.
     */
    public static void setContextMap(Map<String, String> contextMap) {
        if (contextMap == null) {
            clear();
        } else {
            context.get().clear();
            context.get().putAll(contextMap);
        }
    }
}
