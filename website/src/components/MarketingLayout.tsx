'use client';

import React from 'react';
import { Github, ExternalLink } from 'lucide-react';
import Image from 'next/image';

interface MarketingNavProps {
  transparent?: boolean;
}

export function MarketingNav({ transparent = false }: MarketingNavProps) {
  const handleSmoothScroll = (e: React.MouseEvent<HTMLAnchorElement>, targetId: string) => {
    e.preventDefault();
    document.querySelector(targetId)?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <nav className={`${transparent ? 'absolute inset-x-0 top-0' : 'bg-dark-slate/95 border-b border-hermes-blue/30'} relative z-50 px-6 py-4 backdrop-blur-md`}>
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <a href="/" className="hover:opacity-80 transition-opacity flex items-center gap-3">
          <Image src="/favicon.svg" alt="Hermes" width={32} height={32} className="w-8 h-8" />
          <span className="text-xl font-bold text-cream">Hermes</span>
        </a>
        <div className="flex items-center gap-8">
          <a
            href="/#features"
            onClick={(e) => handleSmoothScroll(e, '#features')}
            className="text-slate-gray hover:text-cream transition-colors text-sm font-medium"
          >
            Features
          </a>
          <a
            href="/#how-it-works"
            onClick={(e) => handleSmoothScroll(e, '#how-it-works')}
            className="text-slate-gray hover:text-cream transition-colors text-sm font-medium"
          >
            How It Works
          </a>
          <a
            href="/#use-cases"
            onClick={(e) => handleSmoothScroll(e, '#use-cases')}
            className="text-slate-gray hover:text-cream transition-colors text-sm font-medium"
          >
            Use Cases
          </a>
          <a
            href="https://dotbrains.github.io/hermes"
            className="text-slate-gray hover:text-cream transition-colors text-sm font-medium inline-flex items-center gap-1.5"
            target="_blank"
            rel="noopener noreferrer"
          >
            Docs
            <ExternalLink className="w-3.5 h-3.5" />
          </a>
          <div className="flex items-center gap-3 ml-2">
            <a
              href="https://github.com/dotbrains/hermes"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-2 px-4 py-2 bg-dark-gray hover:bg-dark-slate border border-hermes-blue text-cream rounded-lg transition-colors text-sm font-medium"
            >
              <Github className="w-4 h-4" />
              <span>Star</span>
            </a>
            <a
              href="/#quick-start"
              onClick={(e) => handleSmoothScroll(e, '#quick-start')}
              className="bg-gradient-to-r from-hermes-blue to-hermes-purple hover:from-hermes-purple hover:to-hermes-cyan text-white px-6 py-2 rounded-lg shadow-lg shadow-hermes-blue/30 text-sm font-semibold transition-all"
            >
              Get Started
            </a>
          </div>
        </div>
      </div>
    </nav>
  );
}

export function MarketingFooter() {
  const handleSmoothScroll = (e: React.MouseEvent<HTMLAnchorElement>, targetId: string) => {
    e.preventDefault();
    document.querySelector(targetId)?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <footer className="bg-dark-slate border-t border-hermes-blue/30 py-16">
      <div className="max-w-7xl mx-auto px-6">
        <div className="grid md:grid-cols-4 gap-12 mb-12">
          <div>
            <div className="flex items-center gap-3 mb-4">
              <Image src="/favicon.svg" alt="Hermes" width={32} height={32} className="w-8 h-8" />
              <span className="text-xl font-bold text-cream">Hermes</span>
            </div>
            <p className="text-slate-gray text-sm leading-relaxed mb-4">
              Zero-boilerplate Java logging library with compile-time annotation processing. Open source and free to use.
            </p>
            <div className="flex items-center gap-3">
              <a
                href="https://github.com/dotbrains/hermes"
                className="text-slate-gray hover:text-cream transition-colors"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="GitHub"
              >
                <Github className="w-5 h-5" />
              </a>
            </div>
          </div>
          <div>
            <h4 className="text-cream font-semibold mb-4 text-sm uppercase tracking-wider">Product</h4>
            <ul className="space-y-3">
              <li>
                <a
                  href="/#features"
                  onClick={(e) => handleSmoothScroll(e, '#features')}
                  className="text-slate-gray hover:text-cream text-sm transition-colors inline-block cursor-pointer"
                >
                  Features
                </a>
              </li>
              <li>
                <a
                  href="/#how-it-works"
                  onClick={(e) => handleSmoothScroll(e, '#how-it-works')}
                  className="text-slate-gray hover:text-cream text-sm transition-colors inline-block cursor-pointer"
                >
                  How It Works
                </a>
              </li>
              <li>
                <a
                  href="/#use-cases"
                  onClick={(e) => handleSmoothScroll(e, '#use-cases')}
                  className="text-slate-gray hover:text-cream text-sm transition-colors inline-block cursor-pointer"
                >
                  Use Cases
                </a>
              </li>
              <li>
                <a
                  href="/#quick-start"
                  onClick={(e) => handleSmoothScroll(e, '#quick-start')}
                  className="text-slate-gray hover:text-cream text-sm transition-colors inline-block cursor-pointer"
                >
                  Quick Start
                </a>
              </li>
            </ul>
          </div>
          <div>
            <h4 className="text-cream font-semibold mb-4 text-sm uppercase tracking-wider">Resources</h4>
            <ul className="space-y-3">
              <li>
                <a href="https://dotbrains.github.io/hermes" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  Documentation
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
              <li>
                <a href="https://dotbrains.github.io/hermes/architecture/overview" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  Architecture
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
              <li>
                <a href="https://github.com/dotbrains/hermes/tree/master/hermes-examples" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  Examples
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
            </ul>
          </div>
          <div>
            <h4 className="text-cream font-semibold mb-4 text-sm uppercase tracking-wider">Community</h4>
            <ul className="space-y-3">
              <li>
                <a href="https://github.com/dotbrains/hermes" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  GitHub Repository
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
              <li>
                <a href="https://github.com/dotbrains/hermes/issues" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  Report Issues
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
              <li>
                <a href="https://github.com/dotbrains/hermes/discussions" className="text-slate-gray hover:text-cream text-sm transition-colors inline-flex items-center gap-1.5" target="_blank" rel="noopener noreferrer">
                  Discussions
                  <ExternalLink className="w-3 h-3" />
                </a>
              </li>
            </ul>
          </div>
        </div>
        <div className="pt-8 border-t border-hermes-blue/30 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-slate-gray text-sm">
            © {new Date().getFullYear()} Hermes. All rights reserved.
          </p>
          <p className="text-slate-gray/70 text-xs">
            Open source software licensed under MIT
          </p>
        </div>
      </div>
    </footer>
  );
}
