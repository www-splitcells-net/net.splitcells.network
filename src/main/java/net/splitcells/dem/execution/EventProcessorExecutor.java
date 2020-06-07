package net.splitcells.dem.execution;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
                        this.executeNextTask();
                    }
                }));
        executor.get().start();
    }

    public synchronized void stopAndWaitForExit() {
        enabled = false;
        try {
            if (executor.isPresent()) {
                executor.get().join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor = Optional.empty();
    }

    public synchronized void executeNextTask() {
        try {
            tasks.take().processEvents();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(EventProcessor processor) {
        tasks.add(processor);
    }

    @Override
    public synchronized void close() {
        stopAndWaitForExit();
    }

    @Override
    public void flush() {

    }
}