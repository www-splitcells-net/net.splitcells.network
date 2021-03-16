package net.splitcells.dem.execution;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Deprecated
public class EventProcessorExecutor implements Closeable, Flushable {
    public static EventProcessorExecutor eventProcessorExecutor() {
        return new EventProcessorExecutor();
    }

    private Optional<Thread> executor = Optional.empty();
    private boolean enabled = false;
    private final LinkedBlockingQueue<EventProcessor> tasks = new LinkedBlockingQueue<>();
    private Optional<EventProcessor> currentTask;

    private EventProcessorExecutor() {
    }

    public synchronized void start() {
        enabled = true;
        executor = Optional.of(
                new Thread(() -> {
                    while (enabled) {
                        executeNextTask();
                    }
                }));
        executor.get().start();
    }

    public synchronized void stopAndWaitForExit() {
        enabled = false;
        try {
            if (executor.isPresent()) {
                executor.get().interrupt();
                executor.get().join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor = Optional.empty();
    }

    public void executeNextTask() {
        try {
            tasks.take().processEvents();
        } catch (InterruptedException e) {
            // Nothing is done.
        }
    }

    public void register(EventProcessor processor) {
        tasks.add(processor);
    }

    @Override
    public synchronized void close() {
        flush();
        stopAndWaitForExit();
    }

    /**
     * HACK This blocks all incoming events.
     */
    @Override
    public synchronized void flush() {
        try {
            while (!tasks.isEmpty()) {
                Thread.sleep(500L);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}