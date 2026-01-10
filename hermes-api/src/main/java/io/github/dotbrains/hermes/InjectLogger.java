package io.github.dotbrains.hermes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically inject a Logger field into a class.
 * 
 * <p>When applied to a class, the annotation processor will generate
 * a private static final Logger field named 'log' initialized with
 * LoggerFactory.getLogger(ClassName.class).
 * 
 * <p>Example usage:
 * <pre>
 * {@code
 * @InjectLogger
 * public class UserService {
 *     public void createUser() {
 *         log.info("Creating user");
 *     }
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface InjectLogger {
    
    /**
     * The name of the logger field to generate.
     * Default is "log".
     */
    String value() default "log";
}
