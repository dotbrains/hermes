package io.github.dotbrains.hermes.examples;

import io.github.dotbrains.hermes.InjectLogger;

/**
 * Demo showing annotation processor usage with @InjectLogger.
 * This class will have a base class generated at compile-time with a protected Logger field.
 */
@InjectLogger
public class AnnotationProcessorDemo extends AnnotationProcessorDemoHermesLogger {

    public static void main(String[] args) {
        AnnotationProcessorDemo demo = new AnnotationProcessorDemo();
        demo.demonstrateAnnotationProcessor();
    }

    public void demonstrateAnnotationProcessor() {
        System.out.println("=== @InjectLogger Annotation Processor Demo ===\n");

        // The 'log' field is automatically injected by the annotation processor
        // at compile-time via the generated base class
        log.info("Logger injected via @InjectLogger annotation");
        log.debug("No manual LoggerFactory.getLogger() call needed!");

        // All standard logging methods work
        log.warn("Warning: This logger was created at compile-time");
        log.error("Errors can be logged too");

        // Parameterized logging
        String feature = "zero-boilerplate logging";
        log.info("@InjectLogger provides {}", feature);

        // Exception logging
        try {
            throw new IllegalStateException("Demo exception");
        } catch (Exception e) {
            log.error("Exception caught", e);
        }

        System.out.println("\n=== Demo Complete ===");
        System.out.println("Check target/generated-sources/annotations/ for the generated base class");
    }
}
