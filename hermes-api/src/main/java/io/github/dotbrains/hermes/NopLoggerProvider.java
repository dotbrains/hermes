package io.github.dotbrains.hermes;

import java.util.function.Supplier;

/**
 * No-operation logger provider used as a fallback when no implementation is found.
 */
class NopLoggerProvider implements LoggerProvider {

    @Override
    public Logger getLogger(String name) {
        return new NopLogger(name);
    }

    private static class NopLogger implements Logger {
        private final String name;

        NopLogger(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void trace(String message) {}

        @Override
        public void trace(String message, Object arg) {}

        @Override
        public void trace(String message, Object arg1, Object arg2) {}

        @Override
        public void trace(String message, Object... args) {}

        @Override
        public void trace(String message, Throwable throwable) {}

        @Override
        public void trace(Supplier<String> messageSupplier) {}

        @Override
        public void trace(Marker marker, String message) {}

        @Override
        public void trace(Marker marker, String message, Object... args) {}

        @Override
        public void debug(String message) {}

        @Override
        public void debug(String message, Object arg) {}

        @Override
        public void debug(String message, Object arg1, Object arg2) {}

        @Override
        public void debug(String message, Object... args) {}

        @Override
        public void debug(String message, Throwable throwable) {}

        @Override
        public void debug(Supplier<String> messageSupplier) {}

        @Override
        public void debug(Marker marker, String message) {}

        @Override
        public void debug(Marker marker, String message, Object... args) {}

        @Override
        public void info(String message) {}

        @Override
        public void info(String message, Object arg) {}

        @Override
        public void info(String message, Object arg1, Object arg2) {}

        @Override
        public void info(String message, Object... args) {}

        @Override
        public void info(String message, Throwable throwable) {}

        @Override
        public void info(Supplier<String> messageSupplier) {}

        @Override
        public void info(Marker marker, String message) {}

        @Override
        public void info(Marker marker, String message, Object... args) {}

        @Override
        public void warn(String message) {}

        @Override
        public void warn(String message, Object arg) {}

        @Override
        public void warn(String message, Object arg1, Object arg2) {}

        @Override
        public void warn(String message, Object... args) {}

        @Override
        public void warn(String message, Throwable throwable) {}

        @Override
        public void warn(Supplier<String> messageSupplier) {}

        @Override
        public void warn(Marker marker, String message) {}

        @Override
        public void warn(Marker marker, String message, Object... args) {}

        @Override
        public void error(String message) {}

        @Override
        public void error(String message, Object arg) {}

        @Override
        public void error(String message, Object arg1, Object arg2) {}

        @Override
        public void error(String message, Object... args) {}

        @Override
        public void error(String message, Throwable throwable) {}

        @Override
        public void error(Supplier<String> messageSupplier) {}

        @Override
        public void error(Marker marker, String message) {}

        @Override
        public void error(Marker marker, String message, Object... args) {}
    }
}
