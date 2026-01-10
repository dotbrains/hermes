package io.github.dotbrains.hermes.core.layout;

import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Layout that formats log events using a pattern string.
 * Supports patterns like: %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
 */
public class PatternLayout {
    
    private static final String DEFAULT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
    
    private final String pattern;
    private final DateTimeFormatter dateFormatter;

    public PatternLayout() {
        this(DEFAULT_PATTERN);
    }

    public PatternLayout(String pattern) {
        this.pattern = pattern != null ? pattern : DEFAULT_PATTERN;
        this.dateFormatter = extractDateFormatter(this.pattern);
    }

    /**
     * Formats a log event according to the pattern.
     */
    public String format(LogEvent event) {
        String result = pattern;
        
        // %d - date/time
        if (result.contains("%d")) {
            String datePattern = extractDatePattern(result);
            String formattedDate = event.timestamp()
                .atZone(ZoneId.systemDefault())
                .format(dateFormatter);
            result = result.replaceAll("%d\\{[^}]+\\}|%d", formattedDate);
        }
        
        // %thread - thread name
        result = result.replace("%thread", event.threadName());
        
        // %level - log level
        String levelStr = event.level().name();
        result = result.replace("%-5level", String.format("%-5s", levelStr));
        result = result.replace("%level", levelStr);
        
        // %logger - logger name
        result = replaceLogger(result, event.loggerName());
        
        // %msg - message
        String formattedMessage = MessageFormatter.format(event.message(), event.arguments());
        result = result.replace("%msg", formattedMessage != null ? formattedMessage : "");
        
        // %n - newline
        result = result.replace("%n", System.lineSeparator());
        
        // %mdc - MDC context
        if (result.contains("%mdc") && event.mdcContext() != null && !event.mdcContext().isEmpty()) {
            result = result.replace("%mdc", formatMdc(event.mdcContext()));
        } else {
            result = result.replace("%mdc", "");
        }
        
        // Append exception if present
        if (event.throwable() != null) {
            result += formatThrowable(event.throwable());
        }
        
        return result;
    }

    private String replaceLogger(String pattern, String loggerName) {
        // %logger{36} - abbreviated logger name
        if (pattern.contains("%logger{")) {
            int start = pattern.indexOf("%logger{");
            int end = pattern.indexOf("}", start);
            if (end > start) {
                String lengthStr = pattern.substring(start + 8, end);
                try {
                    int maxLength = Integer.parseInt(lengthStr);
                    String abbreviated = abbreviate(loggerName, maxLength);
                    return pattern.substring(0, start) + abbreviated + pattern.substring(end + 1);
                } catch (NumberFormatException e) {
                    // Fall through to simple replacement
                }
            }
        }
        
        return pattern.replace("%logger", loggerName);
    }

    private String abbreviate(String name, int maxLength) {
        if (name.length() <= maxLength) {
            return name;
        }
        
        String[] parts = name.split("\\.");
        if (parts.length == 1) {
            return name.substring(0, Math.min(maxLength, name.length()));
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            result.append(parts[i].charAt(0)).append('.');
        }
        result.append(parts[parts.length - 1]);
        
        if (result.length() > maxLength) {
            return result.substring(0, maxLength);
        }
        
        return result.toString();
    }

    private String extractDatePattern(String pattern) {
        int start = pattern.indexOf("%d{");
        if (start >= 0) {
            int end = pattern.indexOf("}", start);
            if (end > start) {
                return pattern.substring(start + 3, end);
            }
        }
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }

    private DateTimeFormatter extractDateFormatter(String pattern) {
        String datePattern = extractDatePattern(pattern);
        try {
            return DateTimeFormatter.ofPattern(datePattern);
        } catch (Exception e) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        }
    }

    private String formatMdc(Map<String, String> mdc) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mdc.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    private String formatThrowable(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println();
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
