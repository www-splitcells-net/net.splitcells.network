/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.splitcells.dem.utils.ExecutionException.executionException;

@Deprecated
@JavaLegacyArtifact
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

    public void start() {
        enabled = true;
        executor = Optional.of(
                new Thread(() -> {
                    while (enabled) {
                        executeNextTask();
                    }
                }));
        executor.get().start();
    }

    public void stopAndWaitForExit() {
        enabled = false;
        try {
            if (executor.isPresent()) {
                executor.get().interrupt();
                executor.get().join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        executor = Optional.empty();
    }

    public void executeNextTask() {
        try {
            tasks.take().processEvents();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw executionException(e);
        }
    }

    public void register(EventProcessor processor) {
        tasks.add(processor);
    }

    @Override
    public void close() {
        flush();
        stopAndWaitForExit();
    }

    /**
     * HACK This blocks all incoming events.
     */
    @Override
    public void flush() {
        try {
            while (!tasks.isEmpty()) {
                Thread.sleep(500L);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw executionException(e);
        }
    }
}