package io.github.dotbrains.hermes.core;

/**
 * Formats log messages with {} placeholders.
 */
public final class MessageFormatter {

    private static final String PLACEHOLDER = "{}";
    
    // Thread-local StringBuilder for zero-allocation formatting
    private static final ThreadLocal<StringBuilder> stringBuilder = 
        ThreadLocal.withInitial(() -> new StringBuilder(256));

    private MessageFormatter() {
        // Utility class
    }

    /**
     * Formats a message with arguments.
     * Replaces {} placeholders with the string representation of arguments.
     */
    public static String format(String message, Object[] args) {
        if (message == null) {
            return null;
        }
        
        if (args == null || args.length == 0) {
            return message;
        }

        StringBuilder sb = stringBuilder.get();
        sb.setLength(0); // Clear previous content

        int messageIndex = 0;
        int argIndex = 0;

        while (messageIndex < message.length()) {
            int placeholderIndex = message.indexOf(PLACEHOLDER, messageIndex);
            
            if (placeholderIndex == -1) {
                // No more placeholders, append rest of message
                sb.append(message, messageIndex, message.length());
                break;
            }

            // Append text before placeholder
            sb.append(message, messageIndex, placeholderIndex);

            // Append argument if available
            if (argIndex < args.length) {
                appendArg(sb, args[argIndex]);
                argIndex++;
            } else {
                // No more arguments, keep placeholder
                sb.append(PLACEHOLDER);
            }

            messageIndex = placeholderIndex + PLACEHOLDER.length();
        }

        return sb.toString();
    }

    /**
     * Formats a message with a single argument.
     */
    public static String format(String message, Object arg) {
        return format(message, new Object[]{arg});
    }

    /**
     * Formats a message with two arguments.
     */
    public static String format(String message, Object arg1, Object arg2) {
        return format(message, new Object[]{arg1, arg2});
    }

    private static void appendArg(StringBuilder sb, Object arg) {
        if (arg == null) {
            sb.append("null");
        } else if (arg instanceof String) {
            sb.append(arg);
        } else if (arg.getClass().isArray()) {
            appendArray(sb, arg);
        } else {
            sb.append(arg);
        }
    }

    private static void appendArray(StringBuilder sb, Object array) {
        if (array instanceof Object[]) {
            Object[] objArray = (Object[]) array;
            sb.append('[');
            for (int i = 0; i < objArray.length; i++) {
                if (i > 0) sb.append(", ");
                appendArg(sb, objArray[i]);
            }
            sb.append(']');
        } else if (array instanceof int[]) {
            sb.append(java.util.Arrays.toString((int[]) array));
        } else if (array instanceof long[]) {
            sb.append(java.util.Arrays.toString((long[]) array));
        } else if (array instanceof boolean[]) {
            sb.append(java.util.Arrays.toString((boolean[]) array));
        } else if (array instanceof byte[]) {
            sb.append(java.util.Arrays.toString((byte[]) array));
        } else if (array instanceof char[]) {
            sb.append(java.util.Arrays.toString((char[]) array));
        } else if (array instanceof short[]) {
            sb.append(java.util.Arrays.toString((short[]) array));
        } else if (array instanceof float[]) {
            sb.append(java.util.Arrays.toString((float[]) array));
        } else if (array instanceof double[]) {
            sb.append(java.util.Arrays.toString((double[]) array));
        } else {
            sb.append(array);
        }
    }

    /**
     * Extracts throwable from arguments if present as last element.
     */
    public static Throwable extractThrowable(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        
        Object lastArg = args[args.length - 1];
        return lastArg instanceof Throwable ? (Throwable) lastArg : null;
    }
}
