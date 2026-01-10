'use client';

import { FileCode, Cog, Zap } from 'lucide-react';

export function HowItWorksSection() {
  const steps = [
    {
      icon: <FileCode className="w-8 h-8" />,
      step: '1',
      title: 'Annotate Your Class',
      description: 'Add @InjectLogger and extend the generated base class. The annotation processor creates a parent class with a protected Logger field during compilation.',
    },
    {
      icon: <Cog className="w-8 h-8" />,
      step: '2',
      title: 'Build Your Project',
      description: 'Maven compiles your code and the annotation processor generates the base class. Zero runtime reflection means native-image compatibility out of the box.',
    },
    {
      icon: <Zap className="w-8 h-8" />,
      step: '3',
      title: 'Log Away',
      description: 'Use the inherited log field to write logs. Configure appenders and layouts programmatically or via Spring Boot YAML config.',
    },
  ];

  return (
    <section id="how-it-works" className="py-20 bg-dark-gray/50">
      <div className="max-w-7xl mx-auto px-6">
        <div className="text-center mb-16">
          <h2 className="text-4xl font-bold text-cream mb-4">
            How It Works
          </h2>
          <p className="text-slate-gray text-lg max-w-3xl mx-auto">
            Three simple steps from zero to production-ready logging
          </p>
        </div>
        <div className="grid md:grid-cols-3 gap-8">
          {steps.map((step, index) => (
            <div key={index} className="relative">
              <div className="bg-dark-slate border border-hermes-blue/30 rounded-xl p-8 text-center hover:border-hermes-purple/40 transition-all">
                <div className="w-16 h-16 bg-gradient-to-br from-hermes-blue to-hermes-purple rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4">
                  {step.step}
                </div>
                <div className="w-12 h-12 mx-auto mb-4 text-hermes-blue flex items-center justify-center">
                  {step.icon}
                </div>
                <h3 className="text-xl font-semibold text-cream mb-3">{step.title}</h3>
                <p className="text-slate-gray leading-relaxed">{step.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
