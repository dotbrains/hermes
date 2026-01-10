package io.github.dotbrains.hermes.examples;

import io.github.dotbrains.hermes.Logger;
import io.github.dotbrains.hermes.LoggerFactory;
import io.github.dotbrains.hermes.MDC;

/**
 * Simple demo application showing Hermes in action.
 */
public class Demo {
    
    private static final Logger log = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        System.out.println("=== Hermes Logging Demo ===\n");
        
        // Basic logging
        log.trace("This is a TRACE message (won't show with default INFO level)");
        log.debug("This is a DEBUG message (won't show with default INFO level)");
        log.info("This is an INFO message");
        log.warn("This is a WARN message");
        log.error("This is an ERROR message");
        
        System.out.println();
        
        // Parameterized logging
        String username = "john.doe";
        int userId = 12345;
        log.info("User {} logged in with ID {}", username, userId);
        
        System.out.println();
        
        // MDC (Mapped Diagnostic Context)
        MDC.put("requestId", "req-789");
        MDC.put("sessionId", "sess-456");
        log.info("Processing request with MDC context");
        MDC.clear();
        
        System.out.println();
        
        // Exception logging
        try {
            throw new RuntimeException("Something went wrong!");
        } catch (Exception e) {
            log.error("Failed to process request", e);
        }
        
        System.out.println();
        
        // Conditional logging (for expensive operations)
        if (log.isDebugEnabled()) {
            log.debug("This expensive computation result: {}", expensiveOperation());
        } else {
            log.info("Debug is disabled, skipping expensive operation");
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    private static String expensiveOperation() {
        // Simulate expensive computation
        return "computed-value";
    }
}
