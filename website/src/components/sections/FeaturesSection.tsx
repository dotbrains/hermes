'use client';

import { Zap, Code, Package, Cpu, Layers, Rocket } from 'lucide-react';

export function FeaturesSection() {
  const features = [
    {
      icon: <Code className="w-6 h-6" />,
      title: '@InjectLogger Annotation',
      description: 'Zero-boilerplate logging with compile-time annotation processing. No reflection, no runtime overhead.',
    },
    {
      icon: <Zap className="w-6 h-6" />,
      title: 'LMAX Disruptor Async',
      description: 'Lock-free ring buffer for 10M+ logs/sec throughput. Non-blocking publish with background processing.',
    },
    {
      icon: <Package className="w-6 h-6" />,
      title: 'Spring Boot Integration',
      description: 'Auto-configuration via starter module. YAML config binding and actuator health indicators included.',
    },
    {
      icon: <Layers className="w-6 h-6" />,
      title: 'Kotlin DSL',
      description: 'Idiomatic Kotlin extensions with lazy evaluation, MDC DSL, and structured logging builders.',
    },
    {
      icon: <Cpu className="w-6 h-6" />,
      title: 'Multiple Appenders',
      description: 'Console, File, Rolling, Async, Logstash. Pattern and JSON layouts with ThreadLocal optimization.',
    },
    {
      icon: <Rocket className="w-6 h-6" />,
      title: 'GraalVM Native Image',
      description: 'Native-image metadata included. Compile-time processing eliminates reflection requirements.',
    },
  ];

  return (
    <section id="features" className="py-12 sm:py-16 lg:py-20 bg-dark-slate">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-10 sm:mb-16">
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-cream mb-3 sm:mb-4">
            Built for Modern Java Development
          </h2>
          <p className="text-cream/70 text-base sm:text-lg lg:text-xl max-w-3xl mx-auto">
            Hermes combines compile-time magic with runtime performance to deliver a logging experience that just works
          </p>
        </div>
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6 lg:gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="group bg-dark-gray/50 border border-hermes-blue/20 hover:border-hermes-purple/40 rounded-xl p-5 sm:p-6 transition-all hover:shadow-lg hover:shadow-hermes-blue/10"
            >
              <div className="w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-br from-hermes-blue to-hermes-purple rounded-lg flex items-center justify-center text-white mb-3 sm:mb-4 group-hover:scale-110 transition-transform">
                {feature.icon}
              </div>
              <h3 className="text-lg sm:text-xl font-semibold text-cream mb-2">{feature.title}</h3>
              <p className="text-cream/60 text-sm sm:text-base leading-relaxed">{feature.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
