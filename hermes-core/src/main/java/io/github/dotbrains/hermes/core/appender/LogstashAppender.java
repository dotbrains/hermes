package io.github.dotbrains.hermes.core.appender;

import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.layout.JsonLayout;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Appender that sends log events to Logstash over TCP.
 * Uses JSON format and maintains a persistent connection.
 */
public class LogstashAppender implements Appender {
    
    private static final int DEFAULT_PORT = 5000;
    private static final int QUEUE_SIZE = 1000;
    
    private final String name;
    private final String host;
    private final int port;
    private final JsonLayout layout;
    private final BlockingQueue<String> queue;
    
    private Socket socket;
    private BufferedWriter writer;
    private Thread senderThread;
    private volatile boolean started = false;
    private volatile boolean running = false;

    public LogstashAppender(String name, String host) {
        this(name, host, DEFAULT_PORT);
    }

    public LogstashAppender(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.layout = new JsonLayout(); // Logstash expects JSON
        this.queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    }

    @Override
    public void start() {
        if (started) {
            return;
        }
        
        try {
            connect();
            startSenderThread();
            started = true;
        } catch (IOException e) {
            System.err.println("Failed to start LogstashAppender: " + e.getMessage());
        }
    }

    private void connect() throws IOException {
        socket = new Socket(host, port);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void startSenderThread() {
        running = true;
        senderThread = new Thread(() -> {
            while (running) {
                try {
                    String message = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (message != null) {
                        sendMessage(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    System.err.println("Failed to send log to Logstash: " + e.getMessage());
                    // Try to reconnect
                    tryReconnect();
                }
            }
        }, "hermes-logstash-sender");
        senderThread.setDaemon(true);
        senderThread.start();
    }

    private void sendMessage(String message) throws IOException {
        if (writer == null) {
            connect();
        }
        writer.write(message);
        writer.flush();
    }

    private void tryReconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            Thread.sleep(1000); // Wait before reconnecting
            connect();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to reconnect to Logstash: " + e.getMessage());
        }
    }

    @Override
    public void append(LogEvent event) {
        if (!started) {
            return;
        }
        
        String json = layout.format(event);
        
        // Non-blocking add to queue
        if (!queue.offer(json)) {
            System.err.println("Logstash queue full, dropping log event");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void stop() {
        started = false;
        running = false;
        
        // Wait for sender thread to finish
        if (senderThread != null) {
            try {
                senderThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Close connections
        try {
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing LogstashAppender: " + e.getMessage());
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
