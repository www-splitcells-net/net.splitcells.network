/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import java.util.Optional;

/**
 * This is used, in order to provide a final reference to a replaceable value.
 * Thereby, replaceable values for {@link Configuration#configValue(Class)} are supported,
 * which comes in handy, when certain config data needs to be changed during the runtime.
 * In other words, this class signals and makes it clear,
 * that a {@link Configuration#configValue(Class)} can change at any time.
 *
 * @param <T>
 */
public class Variable<T> {
    public static <T> Variable<T> variable() {
        return new Variable<>();
    }

    public static <T> Variable<T> variable(T arg) {
        return new Variable<T>().withValue(arg);
    }

    public static <T> Variable<T> create() {
        return variable();
    }

    private Optional<T> value = Optional.empty();

    private Variable() {

    }

    public Optional<T> value() {
        return value;
    }

    public T val() {
        return value.orElseThrow();
    }

    public Variable<T> withValue(Optional<T> arg) {
        value = arg;
        return this;
    }

    public Variable<T> withValue(T arg) {
        value = Optional.of(arg);
        return this;
    }

    public boolean hasValue() {
        return value.isPresent();
    }

    public boolean isNull() {
        return value.isEmpty();
    }
}
