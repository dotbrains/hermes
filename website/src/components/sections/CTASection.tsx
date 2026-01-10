'use client';

import { Github, BookOpen, MessageCircle } from 'lucide-react';

export function CTASection() {
  return (
    <section className="py-20 bg-gradient-to-br from-hermes-blue/10 via-dark-slate to-dark-slate">
      <div className="max-w-5xl mx-auto px-6 text-center">
        <h2 className="text-4xl sm:text-5xl font-bold text-cream mb-6">
          Ready to Get Started?
        </h2>
        <p className="text-lg text-slate-gray mb-12 max-w-3xl mx-auto">
          Join the community and start building with zero-boilerplate Java logging today
        </p>
        <div className="grid sm:grid-cols-3 gap-6">
          <a
            href="https://github.com/dotbrains/hermes"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-dark-gray/50 border border-hermes-blue/30 hover:border-hermes-blue rounded-xl p-8 transition-all group hover:shadow-lg hover:shadow-hermes-blue/20"
          >
            <div className="w-14 h-14 bg-gradient-to-br from-hermes-blue to-hermes-purple rounded-lg flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform">
              <Github className="w-7 h-7 text-white" />
            </div>
            <h3 className="text-xl font-semibold text-cream mb-2">View on GitHub</h3>
            <p className="text-slate-gray text-sm">Star the repo, fork it, and contribute</p>
          </a>
          <a
            href="https://dotbrains.github.io/hermes"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-dark-gray/50 border border-hermes-purple/30 hover:border-hermes-purple rounded-xl p-8 transition-all group hover:shadow-lg hover:shadow-hermes-purple/20"
          >
            <div className="w-14 h-14 bg-gradient-to-br from-hermes-purple to-hermes-cyan rounded-lg flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform">
              <BookOpen className="w-7 h-7 text-white" />
            </div>
            <h3 className="text-xl font-semibold text-cream mb-2">Read the Docs</h3>
            <p className="text-slate-gray text-sm">Complete guides and API reference</p>
          </a>
          <a
            href="https://github.com/dotbrains/hermes/discussions"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-dark-gray/50 border border-hermes-cyan/30 hover:border-hermes-cyan rounded-xl p-8 transition-all group hover:shadow-lg hover:shadow-hermes-cyan/20"
          >
            <div className="w-14 h-14 bg-gradient-to-br from-hermes-cyan to-hermes-blue rounded-lg flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform">
              <MessageCircle className="w-7 h-7 text-white" />
            </div>
            <h3 className="text-xl font-semibold text-cream mb-2">Join Discussion</h3>
            <p className="text-slate-gray text-sm">Ask questions and share ideas</p>
          </a>
        </div>
      </div>
    </section>
  );
}
