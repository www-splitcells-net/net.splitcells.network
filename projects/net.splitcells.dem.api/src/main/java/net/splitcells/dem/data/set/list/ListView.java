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

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Extend interface with functional write methods: https://www.vavr.io/vavr-docs/#_list
 */
@JavaLegacy
public interface ListView<T> extends Collection<T>, java.util.List<T>, Thing, SetT<T> {

    default List<T> shallowCopy() {
        final List<T> shallowCopy = Lists.list();
        shallowCopy.addAll(this);
        return shallowCopy;
    }

    default void requireComplianceByEveryElementWith(Predicate<T> constraint) {
        stream().filter(e -> !constraint.test(e)).findFirst().ifPresent(e -> {
            throw ExecutionException.execException("List element `" + e + "` does not comply with `" + constraint + "`.");
        });
    }

    default void requirePresenceOf(T element) {
        if (!contains(element)) {
            throw ExecutionException.execException("Expecting `" + this + "` to contain `" + element + "`, but it is not present.");
        }
    }

    default void requireContainsOneOf(T... arg) {
        if (!Arrays.stream(arg).map(this::contains).filter(a -> a).findFirst().orElse(false)) {
            throw ExecutionException.execException("Expecting `"
                    + this
                    + "` to contain any element of `"
                    + listWithValuesOf(arg)
                    + "`, but does not contain any of them.");
        }
    }

    default ListView<T> requireEmpty() {
        if (!isEmpty()) {
            throw ExecutionException.execException("Expecting list to be empty, but is not: " + this);
        }
        return this;
    }

    default void requireSizeOf(int arg) {
        if (size() != arg) {
            throw ExecutionException.execException("List should be size of " + arg + " but has size of " + size() + " instead: " + this);
        }
    }

    default void requireEquals(List<T> arg) {
        assertThat(this).isEqualTo(arg);
    }

    default ListView<T> requireEquality(List<T> arg, Comparison<T> comparison) {
        requireSizeOf(arg.size());
        range(0, size()).forEach(i -> {
            if (comparison.compareTo(get(i), arg.get(i)) != EQUAL) {
                throw execException(tree("This lists is not equal to given list, even though they should.")
                        .withProperty("This list", toString())
                        .withProperty("Given list", arg.toString())
                        .withProperty("First index of unequal content", "" + i)
                        .withProperty("Comparison", comparison.toString())
                );
            }
        });
        return this;
    }

    default T requireLastValue() {
        return get(size() - 1);
    }

    default ListView<T> getRemovedUntilExcludedIndex(int excludedIndex) {
        if (size() == excludedIndex) {
            return this;
        }
        final var list = listWithValuesOf(this);
        while (list.size() >= excludedIndex) {
            list.withRemovedFromBehind(0);
        }
        return this;
    }

    default Flow<T> flow() {
        return Flows.flow(stream());
    }
}
