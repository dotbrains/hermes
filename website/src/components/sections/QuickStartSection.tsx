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
    <section id="quick-start" className="py-20 bg-dark-gray/50">
      <div className="max-w-6xl mx-auto px-6">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-cream mb-4">
            Quick Start
          </h2>
          <p className="text-slate-gray text-lg max-w-3xl mx-auto">
            Add Hermes to your project in seconds
          </p>
        </div>
        <div className="grid lg:grid-cols-2 gap-12">
          <div>
            <h3 className="text-2xl font-bold text-cream mb-4">1. Add Dependencies</h3>
            <div className="flex gap-3 mb-4">
              <button
                onClick={() => setBuildTool('maven')}
                className={`px-4 py-2 rounded-lg font-medium transition-all ${
                  buildTool === 'maven'
                    ? 'bg-gradient-to-r from-hermes-blue to-hermes-purple text-white'
                    : 'bg-dark-slate text-slate-gray hover:text-cream border border-hermes-blue/30'
                }`}
              >
                Maven
              </button>
              <button
                onClick={() => setBuildTool('gradle')}
                className={`px-4 py-2 rounded-lg font-medium transition-all ${
                  buildTool === 'gradle'
                    ? 'bg-gradient-to-r from-hermes-blue to-hermes-purple text-white'
                    : 'bg-dark-slate text-slate-gray hover:text-cream border border-hermes-blue/30'
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
          <div>
            <h3 className="text-2xl font-bold text-cream mb-4">2. Use @InjectLogger</h3>
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
            <div className="mt-6 bg-dark-slate border border-hermes-blue/30 rounded-lg p-4">
              <p className="text-slate-gray text-sm">
                <span className="text-hermes-blue font-semibold">Pro tip:</span> The annotation processor will generate <code className="text-hermes-cyan">UserServiceHermesLogger</code> with a protected <code className="text-hermes-cyan">log</code> field during compilation.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
