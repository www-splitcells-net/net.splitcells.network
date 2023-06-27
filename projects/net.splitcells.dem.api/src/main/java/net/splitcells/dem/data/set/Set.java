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
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

@JavaLegacyArtifact
public interface Set<T> extends java.util.Set<T>, SetT<T> {

    default T removeAny() {
        final T rVal = iterator().next();
        remove(rVal);
        return rVal;
    }

    default boolean add(T e) {
        if (contains(e)) {
            throw new IllegalArgumentException("Element " + e + " already present in " + this);
        }
        ensureContains(e);
        return true;
    }

    void ensureContains(T e);

    default void addAll(T... objects) {
        Arrays.stream(objects).forEach(e -> add(e));
    }

    default boolean containsAny(T... objects) {
        boolean rVal = false;
        final var containment = Arrays.stream(objects)
                .map(e -> contains(e))
                .reduce((a, b) -> a || b);
        return containment.orElse(false);
    }

    default boolean containsAny(Set<T> objects) {
        final var containment = objects.stream()
                .map(e -> contains(e))
                .reduce((a, b) -> a || b);
        return containment.orElse(false);
    }

    default Set<T> with(T... args) {
        addAll(asList(args));
        return this;
    }

    default Set<T> with(Collection<T> args) {
        addAll(args);
        return this;
    }

    @Override
    default boolean remove(Object arg) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            if (!contains(arg)) {
                throw executionException("" + arg);
            }
        }
        ensureRemoved((T) arg);
        return true;
    }

    void ensureRemoved(T arg);

    default void delete(T arg) {
        if (!remove(arg)) {
            throw new IllegalArgumentException("" + arg);
        }
    }

    /**
     * Determines if actions on this {@link Set} are deterministic.
     * <p>
     * This is only used in order to test {@link Set} factories.
     *
     * @return Is this determinstic.
     */
    default Optional<Boolean> _isDeterministic() {
        return Optional.empty();
    }

    default Bool hasOnlyOnce(T arg) {
        final var argCount = stream()
                .filter(e -> e.equals(arg))
                .count();
        return Bools.bool(argCount <= 1);
    }

    default void assertSizeIs(int expectedSize) {
        assertThat(this).hasSize(expectedSize);
    }

    default void requirePresenceOf(T element) {
        if (!contains(element)) {
            throw executionException("Expecting `" + this + "` to contain `" + element + "`, but it is not present.");
        }
    }

    default void requireAbsenceOf(T element) {
        if (contains(element)) {
            throw executionException("Expecting `" + this + "` to not contain `" + element + "`, but it is present.");
        }
    }
}
