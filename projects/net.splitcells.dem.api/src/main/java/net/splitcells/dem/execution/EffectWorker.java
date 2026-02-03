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

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.communication.log.Logs;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * Processes {@link #events}, that are sent to an  {@link #subject} asynchronously.
 *
 * @param <Subject> The subject's class that processes the given events asynchronously.
 */
@JavaLegacy
public class EffectWorker<Subject> implements ExplicitEffect<Subject> {
    public static <S> EffectWorker<S> effectWorker(S subject) {
        return effectWorker(subject, new ArrayBlockingQueue<>(100, true));
    }

    public static <S> EffectWorker<S> effectWorker(S subject, BlockingQueue<Consumer<S>> events) {
        return new EffectWorker<>(subject, events);
    }

    private final BlockingQueue<Consumer<Subject>> events;
    private final Subject subject;

    private EffectWorker(Subject argSubject, BlockingQueue<Consumer<Subject>> argEvents) {
        events = argEvents;
        subject = argSubject;
        executeThread("Effect Worker", () -> {
            while (true) {
                try {
                    final var nextEvent = events.poll(365, TimeUnit.DAYS);
                    if (nextEvent != null) {
                        nextEvent.accept(subject);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw execException(e);
                } catch (Throwable th) {
                    /**
                     * TODO Sometimes {@link Logs#logs()} does not work. Maybe logging is currently not thread safe?
                     */
                    th.printStackTrace();
                    logs().fail(th);
                }
            }
        });
    }

    @Override
    public void affect(Consumer<Subject> event) {
        events.add(event);
    }
}
