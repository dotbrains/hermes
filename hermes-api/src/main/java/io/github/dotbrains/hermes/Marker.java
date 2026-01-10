package io.github.dotbrains.hermes;

/**
 * Markers are named objects used to enrich log messages.
 * They can be used for filtering or triggering specific appenders.
 */
public interface Marker {
    
    /**
     * Gets the name of this marker.
     */
    String getName();
    
    /**
     * Checks if this marker contains the specified marker.
     */
    boolean contains(Marker other);
    
    /**
     * Adds a marker as a child of this marker.
     */
    void add(Marker marker);
    
    /**
     * Removes a child marker.
     */
    boolean remove(Marker marker);
}
