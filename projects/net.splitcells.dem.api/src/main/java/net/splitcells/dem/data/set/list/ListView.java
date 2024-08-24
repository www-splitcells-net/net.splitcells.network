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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static net.splitcells.dem.utils.ExecutionException.executionException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Extend interface with functional write methods: https://www.vavr.io/vavr-docs/#_list
 */
@JavaLegacyArtifact
public interface ListView<T> extends Collection<T>, java.util.List<T>, Thing {
    /**
     * This helper method makes it easier to distinguish {@code isEmpty} and {@code !isEmpty}.
     *
     * @return Whether this list has a size bigger than zero.
     */
    default boolean hasElements() {
        return !isEmpty();
    }

    default void requireComplianceByEveryElementWith(Predicate<T> constraint) {
        stream().filter(e -> !constraint.test(e)).findFirst().ifPresent(e -> {
            throw executionException("List element `" + e + "` does not comply with `" + constraint + "`.");
        });
    }

    default void requirePresenceOf(T element) {
        if (!contains(element)) {
            throw executionException("Expecting `" + this + "` to contain `" + element + "`, but it is not present.");
        }
    }

    default void requireContainsOneOf(T... arg) {
        if (!Arrays.stream(arg).map(this::contains).filter(a -> a).findFirst().orElse(false)) {
            throw executionException("Expecting `"
                    + this
                    + "` to contain any element of `"
                    + arg
                    + "`, but does not contain any of them.");
        }
    }

    default void requireEmpty() {
        if (!isEmpty()) {
            throw executionException("Expecting list to be empty, but is not: " + this);
        }
    }

    default void requireSizeOf(int arg) {
        if (size() != arg) {
            throw executionException("List should be size of " + arg + " but has size of " + size() + " instead: " + this);
        }
    }

    default void requireEquals(List<T> arg) {
        assertThat(this).isEqualTo(arg);
    }

    default T requireLastValue() {
        return get(size() - 1);
    }

    default ListView<T> getRemovedUntilExcludedIndex(int excludedIndex) {
        if (size() == excludedIndex) {
            return this;
        }
        final var list = Lists.listWithValuesOf(this);
        while (list.size() >= excludedIndex) {
            list.withRemovedFromBehind(0);
        }
        return this;
    }
}
