package io.github.dotbrains.hermes.core.appender;

import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.layout.PatternLayout;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Appender that writes log events to a file.
 */
public class FileAppender implements Appender {
    
    private final String name;
    private final PatternLayout layout;
    private final Path filePath;
    private BufferedWriter writer;
    private volatile boolean started = false;

    public FileAppender(String name, String filePath) {
        this(name, filePath, new PatternLayout());
    }

    public FileAppender(String name, String filePath, PatternLayout layout) {
        this.name = name;
        this.filePath = Paths.get(filePath);
        this.layout = layout;
    }

    @Override
    public void start() {
        try {
            // Create parent directories if they don't exist
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            
            writer = new BufferedWriter(new FileWriter(filePath.toFile(), true));
            started = true;
        } catch (IOException e) {
            System.err.println("Failed to start FileAppender: " + e.getMessage());
        }
    }

    @Override
    public void append(LogEvent event) {
        if (!started || writer == null) {
            return;
        }
        
        try {
            String formatted = layout.format(event);
            writer.write(formatted);
            writer.flush(); // Ensure immediate write
        } catch (IOException e) {
            System.err.println("Failed to write log event: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void stop() {
        started = false;
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close FileAppender: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
