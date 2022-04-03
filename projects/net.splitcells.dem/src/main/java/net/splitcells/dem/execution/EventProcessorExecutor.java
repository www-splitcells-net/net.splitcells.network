/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.execution;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Deprecated
public class EventProcessorExecutor implements Resource {
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