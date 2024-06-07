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

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.StreamUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public interface SetT<T> extends Collection<T> {

    /**
     * This helper method makes it easier to distinguish {@code isEmpty} and {@code !isEmpty}.
     *
     * @return Whether this list has a size bigger than zero.
     */
    default boolean hasElements() {
        return !isEmpty();
    }

    default <R> List<R> mapped(Function<T, R> mapper) {
        return Lists.<R>list().withAppended(
                stream().map(mapper).collect(Lists.toList())
        );
    }

    default <R> List<R> flatMapped(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return Lists.<R>list().withAppended(
                stream().flatMap(mapper).collect(Lists.toList())
        );
    }

    default Optional<T> reduced(BinaryOperator<T> accumulator) {
        return stream().reduce(accumulator);
    }

    default boolean hasDuplicates() {
        final java.util.Set<T> uniques = new HashSet<>();
        for (T e : this) {
            if (uniques.contains(e)) {
                return true;
            }
            uniques.add(e);
        }
        return false;
    }

    default void requireUniqueness() {
        Bools.require(!this.hasDuplicates());
    }

    default void requireSetSizeOf(int requiredSize) {
        if (size() != requiredSize) {
            throw executionException("Set needs to have " + requiredSize + " elements, but has " + size() + " elements instead: " + this);
        }
    }

    default void requireEmptySet() {
        if (!isEmpty()) {
            throw executionException("Set should be empty, but has " + size() + " elements instead: " + this);
        }
    }

    default void requireAnyContent() {
        if (isEmpty()) {
            throw executionException("Set should have any content, but has no elements instead: " + this);
        }
    }

    default void requireContentsOf(T... content) {
        StreamUtils.stream(content).forEach(c -> {
            if (!contains(c)) {
                throw executionException("Set should contain following contents in any order, but does not: set="
                        + this
                        + ", contents="
                        + listWithValuesOf(content));
            }
        });
    }

    default void requireContentsOf(BiPredicate<T, T> comparer, T... requiredContent) {
        requireContentsOf(comparer, setOfUniques(requiredContent));
    }

    default void requireContentsOf(BiPredicate<T, T> comparer, Collection<T> requiredContent) {
        requiredContent.forEach(c -> {
            final var contains = stream().map(t -> comparer.test(t, c)).filter(t -> t).findFirst().orElse(false);
            if (!contains) {
                throw executionException(perspective("Set should contain following contents in any order, but does not.")
                        .withProperty("this", toString())
                        .withProperty("required content", listWithValuesOf(requiredContent).toString())
                        .withProperty("missing content element", c.toString())
                );
            }
        });
    }


    default boolean hasContentOf(BiPredicate<T, T> comparer, Collection<T> requiredContent) {
        return requiredContent.stream().map(c -> stream()
                        .map(t -> comparer.test(t, c))
                        .filter(t -> t)
                        .findFirst()
                        .orElse(false))
                .filter(c -> c)
                .findFirst()
                .orElse(false);
    }

    default void requireContentsOf(SetT<T> content) {
        content.forEach(c -> {
            if (!contains(c)) {
                throw executionException("Set should contents in any order, but does not: set="
                        + this
                        + ", contents="
                        + content);
            }
        });
    }
}
