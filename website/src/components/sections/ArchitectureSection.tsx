'use client';

import { Box, Code, Cog, Package, Layers, BookOpen } from 'lucide-react';

export function ArchitectureSection() {
  const modules = [
    {
      icon: <Box className="w-5 h-5" />,
      name: 'hermes-api',
      description: 'Core interfaces and annotations (Logger, LoggerFactory, @InjectLogger, MDC, Marker)',
    },
    {
      icon: <Cog className="w-5 h-5" />,
      name: 'hermes-core',
      description: 'HermesLogger implementation, appenders (Console, File, Rolling, Async, Logstash), layouts (Pattern, JSON)',
    },
    {
      icon: <Code className="w-5 h-5" />,
      name: 'hermes-processor',
      description: 'Compile-time annotation processor that generates base classes with protected Logger fields',
    },
    {
      icon: <Package className="w-5 h-5" />,
      name: 'hermes-spring-boot-starter',
      description: 'Auto-configuration, HermesProperties YAML binding, health indicators',
    },
    {
      icon: <Layers className="w-5 h-5" />,
      name: 'hermes-kotlin',
      description: 'Idiomatic Kotlin DSL extensions, lazy evaluation, MDC DSL, structured logging',
    },
    {
      icon: <BookOpen className="w-5 h-5" />,
      name: 'hermes-examples',
      description: 'Working examples and demonstrations for common scenarios',
    },
  ];

  return (
    <section id="architecture" className="py-12 sm:py-16 lg:py-20 bg-dark-slate">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-cream mb-3 sm:mb-4">
            Module Architecture
          </h2>
          <p className="text-cream/70 text-base sm:text-lg lg:text-xl max-w-3xl mx-auto">
            Six Maven modules with clear separation of concerns
          </p>
        </div>
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          {modules.map((module, index) => (
            <div
              key={index}
              className="bg-dark-gray/50 border border-hermes-blue/20 rounded-xl p-4 sm:p-6 hover:border-hermes-purple/40 transition-all"
            >
              <div className="flex items-start gap-2 sm:gap-3 mb-3">
                <div className="w-8 h-8 sm:w-10 sm:h-10 bg-gradient-to-br from-hermes-blue to-hermes-purple rounded-lg flex items-center justify-center text-white flex-shrink-0">
                  {module.icon}
                </div>
                <h3 className="text-base sm:text-lg font-mono font-semibold text-hermes-blue break-all">{module.name}</h3>
              </div>
              <p className="text-cream/60 text-xs sm:text-sm leading-relaxed">{module.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
