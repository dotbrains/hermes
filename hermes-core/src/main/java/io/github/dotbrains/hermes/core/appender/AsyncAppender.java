package io.github.dotbrains.hermes.core.appender;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.github.dotbrains.hermes.core.LogEvent;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * High-performance async appender using LMAX Disruptor for lock-free logging.
 */
public class AsyncAppender implements Appender {
    
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    
    private final String name;
    private final List<Appender> delegates;
    private final Disruptor<LogEventHolder> disruptor;
    private final RingBuffer<LogEventHolder> ringBuffer;
    private volatile boolean started = false;

    public AsyncAppender(String name, List<Appender> delegates) {
        this(name, delegates, DEFAULT_BUFFER_SIZE);
    }

    @SuppressWarnings("unchecked")
    public AsyncAppender(String name, List<Appender> delegates, int bufferSize) {
        this.name = name;
        this.delegates = delegates;
        
        // Create disruptor with custom thread factory
        ThreadFactory threadFactory = new LoggingThreadFactory();
        this.disruptor = new Disruptor<>(
            LogEventHolder::new,
            bufferSize,
            threadFactory,
            ProducerType.MULTI,
            new YieldingWaitStrategy()
        );
        
        // Set up event handler
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            if (event.logEvent != null) {
                for (Appender delegate : delegates) {
                    if (delegate.isStarted()) {
                        delegate.append(event.logEvent);
                    }
                }
                event.logEvent = null; // Clear for next use
            }
        });
        
        this.ringBuffer = disruptor.getRingBuffer();
    }

    @Override
    public void start() {
        // Start delegate appenders
        for (Appender delegate : delegates) {
            delegate.start();
        }
        
        // Start disruptor
        disruptor.start();
        started = true;
    }

    @Override
    public void append(LogEvent event) {
        if (!started) {
            return;
        }
        
        // Publish to ring buffer
        long sequence = ringBuffer.next();
        try {
            LogEventHolder holder = ringBuffer.get(sequence);
            holder.logEvent = event;
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void stop() {
        started = false;
        
        // Shutdown disruptor (waits for all events to be processed)
        disruptor.shutdown();
        
        // Stop delegate appenders
        for (Appender delegate : delegates) {
            delegate.stop();
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * Holder for log events in the ring buffer.
     */
    private static class LogEventHolder {
        LogEvent logEvent;
    }

    /**
     * Custom thread factory for logging threads.
     */
    private static class LoggingThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LoggingThreadFactory() {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = "hermes-async-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
