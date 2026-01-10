'use client';

import React, { useState } from 'react';
import { CodeBlock } from '@/components/CodeBlock';

export function QuickStartSection() {
  const [buildTool, setBuildTool] = useState<'maven' | 'gradle'>('maven');

  const mavenExample = `<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-api</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-core</artifactId>
    <version>1.0.0</version>
    <scope>runtime</scope>
</dependency>

<!-- For annotation processing -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.github.dotbrains</groupId>
                        <artifactId>hermes-processor</artifactId>
                        <version>1.0.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>`;

  const gradleExample = `dependencies {
    implementation 'io.github.dotbrains:hermes-api:1.0.0'
    runtimeOnly 'io.github.dotbrains:hermes-core:1.0.0'
    annotationProcessor 'io.github.dotbrains:hermes-processor:1.0.0'
}`;

  return (
    <section id="quick-start" className="py-12 sm:py-16 lg:py-20 bg-dark-slate overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-cream mb-3 sm:mb-4">
            Quick Start
          </h2>
          <p className="text-slate-gray text-base sm:text-lg lg:text-xl max-w-3xl mx-auto">
            Add Hermes to your project in seconds
          </p>
        </div>
        <div className="grid lg:grid-cols-2 gap-8 lg:gap-12 items-start">
          <div className="bg-dark-gray/50 rounded-xl p-6 sm:p-8 border border-hermes-blue/20 min-w-0">
            <h3 className="text-xl sm:text-2xl font-bold text-cream mb-4 sm:mb-6">1. Add Dependencies</h3>
            <div className="flex gap-3 mb-6">
              <button
                onClick={() => setBuildTool('maven')}
                className={`flex-1 px-4 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                  buildTool === 'maven'
                    ? 'bg-gradient-to-r from-hermes-blue to-hermes-purple text-white shadow-lg shadow-hermes-blue/30'
                    : 'bg-dark-slate text-slate-gray hover:text-cream hover:border-hermes-blue/50 border border-hermes-blue/30'
                }`}
              >
                Maven
              </button>
              <button
                onClick={() => setBuildTool('gradle')}
                className={`flex-1 px-4 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                  buildTool === 'gradle'
                    ? 'bg-gradient-to-r from-hermes-blue to-hermes-purple text-white shadow-lg shadow-hermes-blue/30'
                    : 'bg-dark-slate text-slate-gray hover:text-cream hover:border-hermes-blue/50 border border-hermes-blue/30'
                }`}
              >
                Gradle
              </button>
            </div>
            <CodeBlock
              code={buildTool === 'maven' ? mavenExample : gradleExample}
              language={buildTool === 'maven' ? 'xml' : 'gradle'}
            />
          </div>
          <div className="bg-dark-gray/50 rounded-xl p-6 sm:p-8 border border-hermes-purple/20 min-w-0">
            <h3 className="text-xl sm:text-2xl font-bold text-cream mb-4 sm:mb-6">2. Use @InjectLogger</h3>
            <CodeBlock
              code={`@InjectLogger
public class UserService 
    extends UserServiceHermesLogger {
    
    public void processUser(User user) {
        log.info("Processing user: {}", 
                 user.getId());
        
        try {
            // Your business logic
            log.debug("User processed");
        } catch (Exception e) {
            log.error("Error: {}", 
                     e.getMessage(), e);
        }
    }
}`}
              language="java"
            />
            <div className="mt-6 bg-hermes-blue/10 border border-hermes-blue/30 rounded-lg p-4 sm:p-5">
              <p className="text-cream text-sm leading-relaxed">
                <span className="text-hermes-blue font-semibold">💡 Pro tip:</span> The annotation processor will generate <code className="bg-dark-slate/80 px-2 py-1 rounded text-hermes-cyan font-mono text-xs">UserServiceHermesLogger</code> with a protected <code className="bg-dark-slate/80 px-2 py-1 rounded text-hermes-cyan font-mono text-xs">log</code> field during compilation.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
