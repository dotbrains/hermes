'use client';

import React, { useState } from 'react';
import { CodeBlock } from '@/components/CodeBlock';

export function CodeExamplesSection() {
  const [activeTab, setActiveTab] = useState<'annotation' | 'spring' | 'kotlin'>('annotation');

  const examples = {
    annotation: `@InjectLogger
public class UserService extends UserServiceHermesLogger {
    public void createUser(String username) {
        log.info("Creating user: {}", username);
        
        try {
            // Business logic here
            log.debug("User created successfully");
        } catch (Exception e) {
            log.error("Failed to create user", e);
        }
    }
}`,
    spring: `# application.yml
hermes:
  level:
    root: INFO
    packages:
      io.github.dotbrains: DEBUG
      com.example: TRACE
  pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  async:
    enabled: true
    queue-size: 1024`,
    kotlin: `class OrderService : OrderServiceHermesLogger() {
    fun processOrder(orderId: String) {
        log.info { "Processing order: $orderId" }
        
        // MDC DSL
        mdc {
            put("orderId", orderId)
            log.debug { "Order context set" }
        }
        
        // Structured logging
        log.info {
            structured {
                "action" to "process_order"
                "orderId" to orderId
                "timestamp" to System.currentTimeMillis()
            }
        }
    }
}`,
  };

  const tabs = [
    { key: 'annotation' as const, label: '@InjectLogger Usage', language: 'java' },
    { key: 'spring' as const, label: 'Spring Boot Config', language: 'yaml' },
    { key: 'kotlin' as const, label: 'Kotlin DSL', language: 'kotlin' },
  ];

  return (
    <section id="code-examples" className="py-20 bg-dark-gray/50">
      <div className="max-w-6xl mx-auto px-6">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-cream mb-4">
            Code Examples
          </h2>
          <p className="text-slate-gray text-lg max-w-3xl mx-auto">
            See how easy it is to use Hermes in your Java or Kotlin projects
          </p>
        </div>
        <div className="bg-dark-slate border border-hermes-blue/30 rounded-xl overflow-hidden">
          <div className="flex border-b border-hermes-blue/30">
            {tabs.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`flex-1 px-6 py-4 text-sm font-medium transition-colors ${
                  activeTab === tab.key
                    ? 'bg-dark-gray/50 text-hermes-blue border-b-2 border-hermes-blue'
                    : 'text-slate-gray hover:text-cream hover:bg-dark-gray/30'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
          <div className="p-6">
            <CodeBlock
              code={examples[activeTab]}
              language={tabs.find((t) => t.key === activeTab)?.language}
            />
          </div>
        </div>
      </div>
    </section>
  );
}
