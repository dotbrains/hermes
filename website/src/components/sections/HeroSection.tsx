'use client';

import React from 'react';
import { Github, FileText, Zap } from 'lucide-react';

interface HeroSectionProps {
  onLearnMore?: () => void;
}

export function HeroSection({ onLearnMore }: HeroSectionProps) {
  return (
    <section className="relative overflow-hidden">
      {/* Background with animated gradient */}
      <div className="absolute inset-0 bg-gradient-to-br from-hermes-blue/10 via-dark-slate to-dark-slate">
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-hermes-purple/20 via-transparent to-transparent"></div>
      </div>

      {/* Hero Content */}
      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 pt-24 sm:pt-32 lg:pt-40 pb-16 sm:pb-24 lg:pb-32">
        <div className="text-center">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-hermes-blue/10 border border-hermes-blue/20 rounded-full mb-4 sm:mb-6">
            <Github className="w-3.5 h-3.5 sm:w-4 sm:h-4 text-hermes-blue" />
            <span className="text-xs sm:text-sm text-hermes-blue font-medium">Open Source • PolyForm Shield 1.0.0</span>
          </div>

          <h1 className="text-3xl sm:text-5xl md:text-6xl lg:text-7xl font-extrabold text-cream leading-tight mb-4 sm:mb-6 px-4">
            Zero-Boilerplate{' '}
            <span className="text-gradient drop-shadow-md">
              Java Logging
            </span>
          </h1>
          <p className="text-base sm:text-lg md:text-xl lg:text-2xl text-cream/70 mb-6 sm:mb-8 leading-relaxed max-w-4xl mx-auto px-4">
            High-performance logging library for Java 17+ with compile-time annotation processing, async logging via LMAX Disruptor, and comprehensive Spring Boot integration. Inspired by SLF4J, built for modern Java.
          </p>
          <div className="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center px-4">
            <a
              href="https://dotbrains.github.io/hermes"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center justify-center gap-2 bg-gradient-to-r from-hermes-blue to-hermes-purple hover:from-hermes-purple hover:to-hermes-cyan text-white px-6 sm:px-8 py-3 sm:py-4 text-base sm:text-lg font-semibold rounded-lg shadow-lg shadow-hermes-blue/30 transition-all"
            >
              <FileText className="w-4 h-4 sm:w-5 sm:h-5" />
              Read the Docs
            </a>
            <a
              href="https://github.com/dotbrains/hermes"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center justify-center gap-2 bg-dark-gray hover:bg-dark-slate text-cream px-6 sm:px-8 py-3 sm:py-4 text-base sm:text-lg font-semibold rounded-lg border border-hermes-blue hover:border-hermes-purple transition-all"
            >
              <Github className="w-4 h-4 sm:w-5 sm:h-5" />
              View on GitHub
            </a>
          </div>
        </div>

        {/* Stats */}
        <div className="mt-12 sm:mt-16 md:mt-24 grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-6 md:gap-8 max-w-5xl mx-auto px-4">
          <div className="bg-dark-gray/50 backdrop-blur-sm border border-hermes-blue/30 rounded-xl p-4 sm:p-6 text-center">
            <div className="text-3xl sm:text-4xl md:text-5xl font-bold text-gradient mb-2">
              Java 17+
            </div>
            <div className="text-cream/60 text-sm sm:text-base md:text-lg">Modern JVM Features</div>
          </div>
          <div className="bg-dark-gray/50 backdrop-blur-sm border border-hermes-purple/30 rounded-xl p-4 sm:p-6 text-center">
            <div className="text-3xl sm:text-4xl md:text-5xl font-bold text-gradient mb-2">
              10M+
            </div>
            <div className="text-cream/60 text-sm sm:text-base md:text-lg">Logs/sec Throughput</div>
          </div>
          <div className="bg-dark-gray/50 backdrop-blur-sm border border-hermes-cyan/30 rounded-xl p-4 sm:p-6 text-center">
            <div className="text-3xl sm:text-4xl md:text-5xl font-bold text-gradient mb-2">
              Zero
            </div>
            <div className="text-cream/60 text-sm sm:text-base md:text-lg">Runtime Reflection</div>
          </div>
        </div>
      </div>
    </section>
  );
}
