/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

import java.util.function.Function;

/**
 * Provides thread safe access to a {@link T} value.
 *
 * @param <T>
 */
public class AtomicEffect<T> {
    public static <T> AtomicEffect<T> atomicEffect(T initValue) {
        return new AtomicEffect<>(initValue);
    }

    private T value;

    private AtomicEffect(T initValue) {
        value = initValue;
    }

    public synchronized T value() {
        return value;
    }

    public synchronized AtomicEffect<T> setValue(T nextValue) {
        value = nextValue;
        return this;
    }

    public synchronized AtomicEffect<T> update(Function<T, T> update) {
        value = update.apply(value);
        return this;
    }
}
