package io.github.dotbrains.hermes.core.appender;

import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.layout.PatternLayout;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appender that writes log events to files with automatic rolling based on file size.
 */
public class RollingFileAppender implements Appender {
    
    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int DEFAULT_MAX_HISTORY = 30;
    
    private final String name;
    private final PatternLayout layout;
    private final Path baseFilePath;
    private final long maxFileSize;
    private final int maxHistory;
    private BufferedWriter writer;
    private long currentFileSize;
    private volatile boolean started = false;

    public RollingFileAppender(String name, String filePath) {
        this(name, filePath, DEFAULT_MAX_FILE_SIZE, DEFAULT_MAX_HISTORY, new PatternLayout());
    }

    public RollingFileAppender(String name, String filePath, long maxFileSize, int maxHistory) {
        this(name, filePath, maxFileSize, maxHistory, new PatternLayout());
    }

    public RollingFileAppender(String name, String filePath, long maxFileSize, int maxHistory, PatternLayout layout) {
        this.name = name;
        this.baseFilePath = Paths.get(filePath);
        this.maxFileSize = maxFileSize;
        this.maxHistory = maxHistory;
        this.layout = layout;
    }

    @Override
    public void start() {
        try {
            // Create parent directories if they don't exist
            Path parent = baseFilePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            
            openWriter();
            started = true;
        } catch (IOException e) {
            System.err.println("Failed to start RollingFileAppender: " + e.getMessage());
        }
    }

    @Override
    public synchronized void append(LogEvent event) {
        if (!started || writer == null) {
            return;
        }
        
        try {
            String formatted = layout.format(event);
            int bytes = formatted.getBytes().length;
            
            // Check if we need to roll
            if (currentFileSize + bytes > maxFileSize) {
                rollFile();
            }
            
            writer.write(formatted);
            writer.flush();
            currentFileSize += bytes;
        } catch (IOException e) {
            System.err.println("Failed to write log event: " + e.getMessage());
        }
    }

    private void openWriter() throws IOException {
        if (Files.exists(baseFilePath)) {
            currentFileSize = Files.size(baseFilePath);
        } else {
            currentFileSize = 0;
        }
        writer = new BufferedWriter(new FileWriter(baseFilePath.toFile(), true));
    }

    private void rollFile() throws IOException {
        // Close current writer
        if (writer != null) {
            writer.close();
        }
        
        // Generate timestamp for rolled file
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String fileName = baseFilePath.getFileName().toString();
        String baseName = fileName.contains(".") 
            ? fileName.substring(0, fileName.lastIndexOf("."))
            : fileName;
        String extension = fileName.contains(".")
            ? fileName.substring(fileName.lastIndexOf("."))
            : "";
        
        // Create rolled file name
        Path rolledFile = baseFilePath.getParent().resolve(baseName + "." + timestamp + extension);
        
        // Move current file to rolled file
        if (Files.exists(baseFilePath)) {
            Files.move(baseFilePath, rolledFile);
        }
        
        // Clean up old files
        cleanupOldFiles();
        
        // Open new writer
        openWriter();
    }

    private void cleanupOldFiles() {
        try {
            String fileName = baseFilePath.getFileName().toString();
            String baseName = fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf("."))
                : fileName;
            String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
            
            Path parent = baseFilePath.getParent();
            if (parent == null) return;
            
            String pattern = baseName + ".*" + extension;
            
            // Find and delete old files beyond maxHistory
            Files.list(parent)
                .filter(p -> p.getFileName().toString().matches(baseName + "\\.\\d{8}-\\d{6}" + extension.replace(".", "\\.")))
                .sorted((a, b) -> b.toFile().lastModified() > a.toFile().lastModified() ? 1 : -1)
                .skip(maxHistory)
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("Failed to delete old log file: " + p);
                    }
                });
        } catch (IOException e) {
            System.err.println("Failed to cleanup old files: " + e.getMessage());
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
                System.err.println("Failed to close RollingFileAppender: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
