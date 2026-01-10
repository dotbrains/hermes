'use client';

import { Server, Package, Zap, Layers, Rocket, Cloud } from 'lucide-react';

export function UseCasesSection() {
  const useCases = [
    {
      icon: <Server className="w-6 h-6" />,
      title: 'Microservices',
      description: 'Lightweight footprint and async logging for high-throughput services with minimal latency impact.',
    },
    {
      icon: <Package className="w-6 h-6" />,
      title: 'Spring Boot Applications',
      description: 'Auto-configuration and YAML binding for seamless integration with Spring Boot ecosystem.',
    },
    {
      icon: <Zap className="w-6 h-6" />,
      title: 'High-Throughput Systems',
      description: 'LMAX Disruptor async appender delivers 10M+ logs/sec without blocking application threads.',
    },
    {
      icon: <Layers className="w-6 h-6" />,
      title: 'Kotlin Projects',
      description: 'Idiomatic Kotlin DSL with lazy evaluation and structured logging for modern JVM applications.',
    },
    {
      icon: <Rocket className="w-6 h-6" />,
      title: 'Native Image Deployments',
      description: 'GraalVM native-image compatible with zero reflection and compile-time processing.',
    },
    {
      icon: <Cloud className="w-6 h-6" />,
      title: 'Cloud-Native Apps',
      description: 'JSON logging with Logstash appender for centralized log aggregation and monitoring.',
    },
  ];

  return (
    <section id="use-cases" className="py-20 bg-dark-slate">
      <div className="max-w-7xl mx-auto px-6">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-cream mb-4">
            Use Cases
          </h2>
          <p className="text-slate-gray text-lg max-w-3xl mx-auto">
            Hermes adapts to your architecture and deployment needs
          </p>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {useCases.map((useCase, index) => (
            <div
              key={index}
              className="bg-dark-gray/50 border border-hermes-blue/20 rounded-xl p-6 hover:border-hermes-purple/40 transition-all"
            >
              <div className="w-12 h-12 bg-gradient-to-br from-hermes-blue to-hermes-purple rounded-lg flex items-center justify-center text-white mb-4">
                {useCase.icon}
              </div>
              <h3 className="text-xl font-semibold text-cream mb-2">{useCase.title}</h3>
              <p className="text-slate-gray leading-relaxed">{useCase.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
