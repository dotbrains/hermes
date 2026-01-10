package io.github.dotbrains.hermes.core;

import io.github.dotbrains.hermes.LogLevel;
import io.github.dotbrains.hermes.Logger;
import io.github.dotbrains.hermes.LoggerProvider;
import io.github.dotbrains.hermes.core.appender.Appender;
import io.github.dotbrains.hermes.core.appender.ConsoleAppender;
import io.github.dotbrains.hermes.core.layout.PatternLayout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of LoggerProvider.
 */
public class HermesLoggerProvider implements LoggerProvider {
    
    private final Map<String, HermesLogger> loggers = new ConcurrentHashMap<>();
    private final List<Appender> appenders;
    private final Map<String, LogLevel> packageLevels = new ConcurrentHashMap<>();
    private volatile LogLevel rootLevel = LogLevel.INFO;

    public HermesLoggerProvider() {
        // Default configuration: console appender with default pattern
        ConsoleAppender consoleAppender = new ConsoleAppender("console", new PatternLayout());
        consoleAppender.start();
        this.appenders = List.of(consoleAppender);
        
        // Load configuration if available
        loadConfiguration();
    }

    @Override
    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, this::createLogger);
    }

    private HermesLogger createLogger(String name) {
        LogLevel level = determineLogLevel(name);
        return new HermesLogger(name, level, appenders);
    }

    private LogLevel determineLogLevel(String loggerName) {
        // Check for exact match
        LogLevel level = packageLevels.get(loggerName);
        if (level != null) {
            return level;
        }

        // Check for package hierarchy
        String currentPackage = loggerName;
        while (currentPackage.contains(".")) {
            int lastDot = currentPackage.lastIndexOf(".");
            currentPackage = currentPackage.substring(0, lastDot);
            level = packageLevels.get(currentPackage);
            if (level != null) {
                return level;
            }
        }

        return rootLevel;
    }

    /**
     * Sets the root log level.
     */
    public void setRootLevel(LogLevel level) {
        this.rootLevel = level;
        // Update existing loggers
        loggers.values().forEach(logger -> {
            if (determineLogLevel(logger.getName()) == level) {
                logger.setLevel(level);
            }
        });
    }

    /**
     * Sets the log level for a specific package.
     */
    public void setPackageLevel(String packageName, LogLevel level) {
        packageLevels.put(packageName, level);
        // Update existing loggers in this package
        loggers.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(packageName))
            .forEach(entry -> entry.getValue().setLevel(determineLogLevel(entry.getKey())));
    }

    private void loadConfiguration() {
        // Try to load from system properties
        String rootLevelProp = System.getProperty("hermes.level.root");
        if (rootLevelProp != null) {
            try {
                setRootLevel(LogLevel.valueOf(rootLevelProp.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid level
            }
        }

        // Load package-specific levels from system properties
        System.getProperties().stringPropertyNames().stream()
            .filter(key -> key.startsWith("hermes.level.") && !key.equals("hermes.level.root"))
            .forEach(key -> {
                String packageName = key.substring("hermes.level.".length());
                String levelValue = System.getProperty(key);
                try {
                    setPackageLevel(packageName, LogLevel.valueOf(levelValue.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid level
                }
            });
    }

    /**
     * Shuts down all appenders.
     */
    public void shutdown() {
        appenders.forEach(Appender::stop);
    }
}
