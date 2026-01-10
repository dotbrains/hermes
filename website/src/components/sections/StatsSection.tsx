'use client';

export function StatsSection() {
  return (
    <section className="py-16 bg-dark-gray/50">
      <div className="max-w-7xl mx-auto px-6">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-cream mb-4">
            Enterprise-grade logging for modern Java
          </h2>
          <p className="text-slate-gray text-lg">
            Open source library with advanced features for production workloads
          </p>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div>
            <div className="text-3xl font-bold text-gradient mb-2">Open Source</div>
            <div className="text-slate-gray">MIT License</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-gradient mb-2">6</div>
            <div className="text-slate-gray">Maven Modules</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-gradient mb-2">Spring Boot</div>
            <div className="text-slate-gray">Auto-Config</div>
          </div>
          <div>
            <div className="text-3xl font-bold text-gradient mb-2">GraalVM</div>
            <div className="text-slate-gray">Native Ready</div>
          </div>
        </div>
      </div>
    </section>
  );
}
