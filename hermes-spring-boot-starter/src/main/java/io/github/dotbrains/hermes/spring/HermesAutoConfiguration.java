package io.github.dotbrains.hermes.spring;

import io.github.dotbrains.hermes.LoggerFactory;
import io.github.dotbrains.hermes.core.HermesLoggerProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Auto-configuration for Hermes logging in Spring Boot.
 */
@AutoConfiguration
@ConditionalOnClass(LoggerFactory.class)
@EnableConfigurationProperties(HermesProperties.class)
public class HermesAutoConfiguration {

    private final HermesProperties properties;
    private HermesLoggerProvider provider;

    public HermesAutoConfiguration(HermesProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void initialize() {
        // Create and configure the logger provider
        provider = new HermesLoggerProvider();
        
        // Set root level
        provider.setRootLevel(properties.getLevel().getRoot());
        
        // Set package-specific levels
        properties.getLevel().getPackages().forEach(provider::setPackageLevel);
        
        // Register the provider
        LoggerFactory.setProvider(provider);
    }

    @PreDestroy
    public void shutdown() {
        if (provider != null) {
            provider.shutdown();
        }
    }

    @Bean
    public HermesLoggingHealthIndicator hermesHealthIndicator() {
        return new HermesLoggingHealthIndicator();
    }
}
