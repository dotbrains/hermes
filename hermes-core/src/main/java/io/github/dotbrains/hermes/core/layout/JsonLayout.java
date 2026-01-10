package io.github.dotbrains.hermes.core.layout;

import io.github.dotbrains.hermes.core.LogEvent;
import io.github.dotbrains.hermes.core.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Layout that formats log events as JSON.
 */
public class JsonLayout {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final boolean prettyPrint;

    public JsonLayout() {
        this(false);
    }

    public JsonLayout(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    /**
     * Formats a log event as JSON.
     */
    public String format(LogEvent event) {
        StringBuilder json = new StringBuilder();
        
        if (prettyPrint) {
            formatPretty(json, event);
        } else {
            formatCompact(json, event);
        }
        
        return json.toString();
    }

    private void formatCompact(StringBuilder json, LogEvent event) {
        json.append("{");
        
        appendField(json, "timestamp", event.timestamp().toString(), false);
        appendField(json, "level", event.level().name(), true);
        appendField(json, "logger", event.loggerName(), true);
        appendField(json, "thread", event.threadName(), true);
        appendField(json, "threadId", String.valueOf(event.threadId()), true);
        
        // Message
        String message = MessageFormatter.format(event.message(), event.arguments());
        appendField(json, "message", escapeJson(message), true);
        
        // MDC
        if (event.mdcContext() != null && !event.mdcContext().isEmpty()) {
            json.append(",\"mdc\":{");
            boolean first = true;
            for (Map.Entry<String, String> entry : event.mdcContext().entrySet()) {
                if (!first) json.append(",");
                appendField(json, entry.getKey(), escapeJson(entry.getValue()), false);
                first = false;
            }
            json.append("}");
        }
        
        // Marker
        if (event.marker() != null) {
            appendField(json, "marker", event.marker().getName(), true);
        }
        
        // Exception
        if (event.throwable() != null) {
            appendField(json, "exception", formatException(event.throwable()), true);
        }
        
        json.append("}\n");
    }

    private void formatPretty(StringBuilder json, LogEvent event) {
        json.append("{\n");
        
        appendFieldPretty(json, "timestamp", event.timestamp().toString(), 1, false);
        appendFieldPretty(json, "level", event.level().name(), 1, true);
        appendFieldPretty(json, "logger", event.loggerName(), 1, true);
        appendFieldPretty(json, "thread", event.threadName(), 1, true);
        appendFieldPretty(json, "threadId", String.valueOf(event.threadId()), 1, true);
        
        // Message
        String message = MessageFormatter.format(event.message(), event.arguments());
        appendFieldPretty(json, "message", escapeJson(message), 1, true);
        
        // MDC
        if (event.mdcContext() != null && !event.mdcContext().isEmpty()) {
            json.append(",\n  \"mdc\": {\n");
            boolean first = true;
            for (Map.Entry<String, String> entry : event.mdcContext().entrySet()) {
                if (!first) json.append(",\n");
                appendFieldPretty(json, entry.getKey(), escapeJson(entry.getValue()), 2, false);
                first = false;
            }
            json.append("\n  }");
        }
        
        // Marker
        if (event.marker() != null) {
            appendFieldPretty(json, "marker", event.marker().getName(), 1, true);
        }
        
        // Exception
        if (event.throwable() != null) {
            appendFieldPretty(json, "exception", formatException(event.throwable()), 1, true);
        }
        
        json.append("\n}\n");
    }

    private void appendField(StringBuilder json, String key, String value, boolean comma) {
        if (comma) json.append(",");
        json.append("\"").append(key).append("\":\"").append(value).append("\"");
    }

    private void appendFieldPretty(StringBuilder json, String key, String value, int indent, boolean comma) {
        if (comma) json.append(",\n");
        json.append("  ".repeat(indent));
        json.append("\"").append(key).append("\": \"").append(value).append("\"");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private String formatException(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return escapeJson(sw.toString());
    }
}
