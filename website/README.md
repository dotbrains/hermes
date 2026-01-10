# Hermes Marketing Website

Public-facing marketing website for Hermes - a zero-boilerplate Java logging library.

## 🚀 Features

- **Hero Section** - Compelling intro with key value props and statistics
- **Stats Section** - Open source highlights and tech stack overview
- **Features Overview** - 6 key features highlighting @InjectLogger, async logging, Spring Boot integration
- **How It Works** - 3-step process explanation
- **Module Architecture** - Breakdown of 6 Maven modules
- **Code Examples** - Interactive code snippets with Java, YAML, and Kotlin examples
- **Use Cases** - 6 practical deployment scenarios
- **Quick Start** - Maven and Gradle setup instructions
- **Modern UI** - Dark theme with blue/purple/cyan gradient accents
- **Responsive Design** - Mobile-first approach
- **Syntax Highlighting** - Code blocks with copy functionality

## 📦 Tech Stack

- **Framework**: Next.js 15 with App Router
- **UI Components**: React 18
- **Styling**: Tailwind CSS 3
- **Syntax Highlighting**: react-syntax-highlighter
- **TypeScript**: Full type safety
- **Icons**: Lucide React

## 🛠️ Setup & Development

### Prerequisites

- Node.js 18+
- pnpm 8+ (or npm/yarn)

### Installation

```bash
# Install dependencies
pnpm install

# Start dev server (default: http://localhost:3003)
pnpm run dev

# Build for production
pnpm run build

# Preview production build
pnpm start
```

### Linting

```bash
# Run ESLint
pnpm run lint

# Fix linting issues
pnpm run lint:fix
```

## 🏗️ Project Structure

```
website/
├── app/
│   ├── layout.tsx        # Root layout with metadata
│   └── page.tsx          # Main landing page
├── src/
│   ├── components/
│   │   ├── CodeBlock.tsx           # Code display with copy button
│   │   ├── MarketingLayout.tsx     # Nav and Footer
│   │   └── sections/
│   │       ├── HeroSection.tsx
│   │       ├── StatsSection.tsx
│   │       ├── FeaturesSection.tsx
│   │       ├── HowItWorksSection.tsx
│   │       ├── ArchitectureSection.tsx
│   │       ├── CodeExamplesSection.tsx
│   │       ├── UseCasesSection.tsx
│   │       ├── QuickStartSection.tsx
│   │       └── CTASection.tsx
│   └── styles/
│       └── globals.css
├── public/                # Static assets
├── tailwind.config.js
├── next.config.js
├── tsconfig.json
├── postcss.config.js
├── .eslintrc.json
└── package.json
```

## 🎨 Page Sections

The website is a single-page marketing site with the following sections:

1. **Hero** - Zero-boilerplate Java logging headline with 3 key stats
2. **Stats** - Open source, 6 modules, Spring Boot, GraalVM ready
3. **Features** - 6 key features with icons (@InjectLogger, async, Spring Boot, Kotlin, appenders, GraalVM)
4. **How It Works** - 3-step workflow (annotate, build, log)
5. **Architecture** - 6 Maven modules breakdown
6. **Code Examples** - Interactive tabs (Java, Spring Boot YAML, Kotlin DSL)
7. **Use Cases** - 6 practical scenarios (microservices, Spring Boot, high-throughput, Kotlin, native, cloud-native)
8. **Quick Start** - Maven/Gradle setup with toggle
9. **CTA** - Final call-to-action with GitHub, docs, and discussions links

## 🎯 Key Features

### Hero Section
- **Gradient Background** - Blue/purple/cyan theme
- **Key Stats** - Java 17+, 10M+ logs/sec, Zero reflection
- **Dual CTAs** - Links to docs and GitHub
- **Open Source Badge** - MIT license prominent

### Interactive Components
- **CodeBlock** - Syntax highlighting with copy button
- **Tabbed Code Examples** - Java, YAML, Kotlin with switching
- **Maven/Gradle Toggle** - Quick start dependency examples

### Content Sections
- **Features** - 6 key features (annotation processing, async, Spring Boot, Kotlin, appenders, native)
- **Architecture** - 6 Maven modules (api, core, processor, spring-boot-starter, kotlin, examples)
- **Code Examples** - @InjectLogger usage, Spring Boot config, Kotlin DSL
- **Use Cases** - Microservices, Spring Boot, high-throughput, Kotlin, native image, cloud-native
- **Quick Start** - Maven and Gradle dependency setup

### Design
- **Dark Theme** - Slate-based palette with blue/purple/cyan gradient accents
- **Responsive** - Mobile-first design
- **Smooth Scrolling** - Anchor link navigation
- **Hover Animations** - Micro-interactions throughout
- **Accurate Content** - All features and examples reflect actual Hermes implementation

## 🌐 External Links

The website links to:

- **GitHub Repository**: https://github.com/dotbrains/hermes
- **GitHub Issues**: https://github.com/dotbrains/hermes/issues
- **GitHub Discussions**: https://github.com/dotbrains/hermes/discussions
- **Documentation**: https://dotbrains.github.io/hermes

## 🎨 Color Palette

The site uses a messenger/Hermes inspired color scheme:

- **Background**: `#1a1d29` (dark-slate)
- **Primary**: `#4a90e2` (hermes-blue)
- **Secondary**: `#7c3aed` (hermes-purple)
- **Accent**: `#06b6d4` (hermes-cyan)
- **Text**: `#fefdfb` (cream)
- **Muted Text**: `#475569` (slate-gray)

## 🐛 Troubleshooting

### Build Errors

If you encounter build errors after updating dependencies:

```bash
rm -rf node_modules .next
rm pnpm-lock.yaml  # or package-lock.json for npm
pnpm install
pnpm run build
```

### Port Conflicts

If port 3003 is already in use, modify the `dev` and `start` scripts in `package.json`:

```json
{
  "scripts": {
    "dev": "next dev -p 3004",
    "start": "next start -p 3004"
  }
}
```

## 📄 License

MIT
