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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Collection;

import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * TODO Extend interface with functional write methods: https://www.vavr.io/vavr-docs/#_list
 */
@JavaLegacyArtifact
public interface ListView<T> extends Collection<T>, java.util.List<T> {
    /**
     * This helper method makes it easier to distinguish {@code isEmpty} and {@code !isEmpty}.
     *
     * @return Whether this list has a size bigger than zero.
     */
    default boolean hasElements() {
        return !isEmpty();
    }

    default void requirePresenceOf(T element) {
        if (!contains(element)) {
            throw executionException("Expecting `" + this + "` to contain `" + element + "`, but it is not present.");
        }
    }

    default void requireEmpty() {
        if (isEmpty()) {
            throw executionException("Expecting list to be empty, but is not: " + this);
        }
    }

    default void requireSizeOf(int arg) {
        if (size() == arg) {
            throw executionException("List should be size of " + arg + " but has size of " + size() + " instead: " + this);
        }
    }
}
