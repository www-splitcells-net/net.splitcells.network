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
package net.splitcells.dem.data.order;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.function.BiFunction;

/**
 * Provides a wrapper for {@link java.util.Comparator} instances.
 *
 * @param <T>
 */
@JavaLegacyArtifact
public class Comparators<T> implements Comparator<T> {

    public static final Comparator<Integer> ASCENDING_INTEGERS = comparator((a, b) -> {
        if (a < b) {
            return Ordering.LESSER_THAN;
        } else if (b < a) {
            return Ordering.GREATER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });
    public static final Comparator<Double> ASCENDING_DOUBLES = comparator((a, b) -> {
        if (a < b) {
            return Ordering.LESSER_THAN;
        } else if (b < a) {
            return Ordering.GREATER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });
    public static final Comparator<Boolean> ASCENDING_BOOLEANS = comparator((a, b) -> {
        if (a && !b) {
            return Ordering.GREATER_THAN;
        } else if (!a && b) {
            return Ordering.LESSER_THAN;
        } else {
            return Ordering.EQUAL;
        }
    });

    public static <T> Comparators<T> legacyComparator(BiFunction<T, T, Integer> comparator) {
        return new Comparators<>(comparator);
    }

    public static <T extends Comparable<T>> Comparators<T> naturalComparator() {
        return legacyComparator((a, b) -> a.compareTo(b));
    }

    private final BiFunction<T, T, Integer> comparator;

    private Comparators(BiFunction<T, T, Integer> comparator) {
        this.comparator = comparator;
    }

    public static <T> Comparator<T> comparator(BiFunction<T, T, Ordering> comparator) {
        return new Comparator<T>() {
            @Override
            public Ordering compareTo(T a, T b) {
                return comparator.apply(a, b);
            }
        };
    }

    /**
     * Create a compatibility wrapper for {@link java.util.Comparator}.
     *
     * @param comparator Comparator Of Java Standard Library
     * @param <T>        Type of Values being Compared
     * @return Wrapped Comparator
     */
    @JavaLegacyArtifact
    public static <T> Comparator<T> comparatorLegacy(BiFunction<T, T, Integer> comparator) {
        return legacyComparator(comparator);

    }

    @Override
    public int compare(T a, T b) {
        return comparator.apply(a, b);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof Comparators) {
            return this.comparator.equals(((Comparators) arg).comparator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return comparator.hashCode();
    }
}
