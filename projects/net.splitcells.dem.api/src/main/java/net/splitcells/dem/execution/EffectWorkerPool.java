/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.execution.EffectWorker.effectWorker;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * Processes {@link #events}, that are sent to an  {@link #subjects} asynchronously via multiple {@link EffectWorker}.
 * This therefore provides a way to process events on multiple threads at once.
 *
 * @param <SUBJECT> The subjects' class that processes the given events asynchronously.
 */
@JavaLegacy
public class EffectWorkerPool<SUBJECT> implements ExplicitEffect<SUBJECT> {
    public static <S> EffectWorkerPool<S> effectWorkerPool(Supplier<S> subjects) {
        return effectWorkerPool(subjects, 100);
    }

    public static <S> EffectWorkerPool<S> effectWorkerPool(Supplier<S> subjects, int maxSubjectCount) {
        return new EffectWorkerPool<>(subjects, maxSubjectCount);
    }

    final Supplier<SUBJECT> subjects;
    /**
     * {@link ArrayBlockingQueue} is used, because it is assumed to be one of the fastest ones in this context.
     */
    private final ArrayBlockingQueue<Consumer<SUBJECT>> events;

    final List<EffectWorker<SUBJECT>> effectWorkers = list();

    int nextWorker = 0;

    private EffectWorkerPool(Supplier<SUBJECT> argSubjects, int maxSubjectCount) {
        if (maxSubjectCount < 1) {
            throw ExecutionException.execException("MaxSubjectCount should be bigger than zero, but is " + maxSubjectCount + " instead.");
        }
        subjects = argSubjects;
        events = new ArrayBlockingQueue<>(100 * maxSubjectCount, true);
        rangeClosed(1, maxSubjectCount).forEach(i -> effectWorkers.add(effectWorker(argSubjects.get(), events)));
    }

    @Override
    public void affect(Consumer<SUBJECT> event) {
        events.add(event);
    }
}
