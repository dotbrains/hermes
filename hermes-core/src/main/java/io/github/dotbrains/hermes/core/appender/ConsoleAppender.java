package io.github.dotbrains.hermes.core.appender;

import io.github.dotbrains.hermes.LogLevel;
import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.layout.PatternLayout;

import java.io.PrintStream;

/**
 * Appender that writes log events to the console (stdout/stderr).
 */
public class ConsoleAppender implements Appender {
    
    private final String name;
    private final PatternLayout layout;
    private final PrintStream out;
    private volatile boolean started = false;

    public ConsoleAppender(String name) {
        this(name, new PatternLayout());
    }

    public ConsoleAppender(String name, PatternLayout layout) {
        this(name, layout, System.out);
    }

    public ConsoleAppender(String name, PatternLayout layout, PrintStream out) {
        this.name = name;
        this.layout = layout;
        this.out = out;
    }

    @Override
    public void append(LogEvent event) {
        if (!started) {
            return;
        }
        
        String formatted = layout.format(event);
        
        // Write ERROR and WARN to stderr, others to configured stream
        if (event.level() == LogLevel.ERROR || event.level() == LogLevel.WARN) {
            System.err.print(formatted);
        } else {
            out.print(formatted);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        out.flush();
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
