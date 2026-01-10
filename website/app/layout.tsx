import type { Metadata } from 'next';
import '@/styles/globals.css';

export const metadata: Metadata = {
  title: 'Hermes - Zero-Boilerplate Java Logging Library',
  description: 'High-performance Java 17+ logging library with compile-time annotation processing, async logging via LMAX Disruptor, and comprehensive Spring Boot integration',
  openGraph: {
    title: 'Hermes - Zero-Boilerplate Java Logging Library',
    description: 'High-performance Java 17+ logging library with compile-time annotation processing, async logging via LMAX Disruptor, and comprehensive Spring Boot integration',
    url: 'https://hermes.dotbrains.io',
    siteName: 'Hermes',
    images: [
      {
        url: '/og-image.svg',
        width: 1200,
        height: 630,
        alt: 'Hermes - Zero-Boilerplate Java Logging Library',
      },
    ],
    locale: 'en_US',
    type: 'website',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Hermes - Zero-Boilerplate Java Logging Library',
    description: 'High-performance Java 17+ logging library with compile-time annotation processing, async logging via LMAX Disruptor, and comprehensive Spring Boot integration',
    images: ['/og-image.svg'],
  },
  icons: {
    icon: [
      {
        url: '/favicon.svg',
        type: 'image/svg+xml',
      },
    ],
    apple: [
      {
        url: '/favicon.svg',
        type: 'image/svg+xml',
      },
    ],
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <head>
        <meta charSet="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      </head>
      <body>{children}</body>
    </html>
  );
}
