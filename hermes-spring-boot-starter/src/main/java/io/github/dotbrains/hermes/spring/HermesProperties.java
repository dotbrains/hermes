package io.github.dotbrains.hermes.spring;

import io.github.dotbrains.hermes.LogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Hermes logging.
 */
@ConfigurationProperties(prefix = "hermes")
public class HermesProperties {

    /**
     * Log levels configuration.
     */
    private Level level = new Level();

    /**
     * Log pattern.
     */
    private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    /**
     * Async logging configuration.
     */
    private Async async = new Async();

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Async getAsync() {
        return async;
    }

    public void setAsync(Async async) {
        this.async = async;
    }

    public static class Level {
        /**
         * Root log level.
         */
        private LogLevel root = LogLevel.INFO;

        /**
         * Package-specific log levels.
         */
        private Map<String, LogLevel> packages = new HashMap<>();

        public LogLevel getRoot() {
            return root;
        }

        public void setRoot(LogLevel root) {
            this.root = root;
        }

        public Map<String, LogLevel> getPackages() {
            return packages;
        }

        public void setPackages(Map<String, LogLevel> packages) {
            this.packages = packages;
        }
    }

    public static class Async {
        /**
         * Whether async logging is enabled.
         */
        private boolean enabled = false;

        /**
         * Async queue size.
         */
        private int queueSize = 1024;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }
    }
}
