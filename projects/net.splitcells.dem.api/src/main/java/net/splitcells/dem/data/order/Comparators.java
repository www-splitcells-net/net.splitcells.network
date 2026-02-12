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

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides a wrapper for {@link java.util.Comparator} instances.
 *
 * @param <T>
 */
@JavaLegacy
public class Comparators<T> implements Comparison<T> {

    public static final Comparison<String> ASCENDING_STRINGS = comparatorLegacy(String::compareTo);
    public static final Comparison<String> DESCENDING_STRINGS = comparatorLegacy((a, b) -> b.compareTo(a));

    public static <T> Comparison<T> ascendingString(Function<T, String> converter) {
        return comparatorLegacy((a, b) -> converter.apply(a).compareTo(converter.apply(b)));
    }

    public static final Comparison<Integer> ASCENDING_INTEGERS = comparator(new BiFunction<Integer, Integer, Ordering>() {
        @Override public Ordering apply(Integer a, Integer b) {
            if (a < b) {
                return Ordering.LESSER_THAN;
            } else if (b < a) {
                return Ordering.GREATER_THAN;
            } else {
                return Ordering.EQUAL;
            }
        }

        @Override public String toString() {
            return "ascending integers";
        }
    });
    public static final Comparison<Double> ASCENDING_DOUBLES = comparator(new BiFunction<Double, Double, Ordering>() {
        @Override public Ordering apply(Double a, Double b) {
            if (a < b) {
                return Ordering.LESSER_THAN;
            } else if (b < a) {
                return Ordering.GREATER_THAN;
            } else {
                return Ordering.EQUAL;
            }
        }

        @Override public String toString() {
            return "ascending doubles";
        }
    });
    public static final Comparison<Boolean> ASCENDING_BOOLEANS = comparator(
            new BiFunction<Boolean, Boolean, Ordering>() {
                @Override public Ordering apply(Boolean a, Boolean b) {
                    if (a && !b) {
                        return Ordering.GREATER_THAN;
                    } else if (!a && b) {
                        return Ordering.LESSER_THAN;
                    } else {
                        return Ordering.EQUAL;
                    }
                }

                @Override public String toString() {
                    return "ascending booleans";
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

    public static <T> Comparison<T> comparator(BiFunction<T, T, Ordering> comparator) {
        return new Comparison<>() {
            @Override public Ordering compareTo(T a, T b) {
                return comparator.apply(a, b);
            }

            @Override public String toString() {
                return comparator.toString();
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
    @JavaLegacy
    public static <T> Comparison<T> comparatorLegacy(BiFunction<T, T, Integer> comparator) {
        return legacyComparator(comparator);

    }

    @Override
    public int compare(T a, T b) {
        return comparator.apply(a, b);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Comparators<?> comparators) {
            return this.comparator.equals(comparators.comparator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return comparator.hashCode();
    }
}
