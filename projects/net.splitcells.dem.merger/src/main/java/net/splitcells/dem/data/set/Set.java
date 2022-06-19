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
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static org.assertj.core.api.Assertions.assertThat;

@JavaLegacyArtifact
public interface Set<T> extends java.util.Set<T>, SetT<T> {

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
        return containsAny();
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
}
