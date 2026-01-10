'use client';

export function StatsSection() {
  return (
    <section className="py-12 sm:py-16 bg-dark-gray/50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="text-center mb-8 sm:mb-12">
          <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-cream mb-3 sm:mb-4">
            Enterprise-grade logging for modern Java
          </h2>
          <p className="text-cream/70 text-base sm:text-lg lg:text-xl">
            Open source library with advanced features for production workloads
          </p>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 sm:gap-6 md:gap-8 text-center">
          <div>
            <div className="text-2xl sm:text-3xl font-bold text-gradient mb-1 sm:mb-2">Open Source</div>
            <div className="text-cream/60 text-sm sm:text-base">MIT License</div>
          </div>
          <div>
            <div className="text-2xl sm:text-3xl font-bold text-gradient mb-1 sm:mb-2">6</div>
            <div className="text-cream/60 text-sm sm:text-base">Maven Modules</div>
          </div>
          <div>
            <div className="text-2xl sm:text-3xl font-bold text-gradient mb-1 sm:mb-2">Spring Boot</div>
            <div className="text-cream/60 text-sm sm:text-base">Auto-Config</div>
          </div>
          <div>
            <div className="text-2xl sm:text-3xl font-bold text-gradient mb-1 sm:mb-2">GraalVM</div>
            <div className="text-cream/60 text-sm sm:text-base">Native Ready</div>
          </div>
        </div>
      </div>
    </section>
  );
}
