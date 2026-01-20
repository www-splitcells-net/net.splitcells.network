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
package net.splitcells.dem.utils;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacyArtifact
public final class MathUtils {

    /**
     * @param target              Each returned sum should amount to this parameter.
     * @param sumComponents       These are all components, which are allowed to be used in order to reach the {@param target} value.
     * @param exactComponentCount Number of components that each valid sum is allowed to have.
     * @return A list of sums, that amount to the given {@param target}.
     * Each element has exactly {@param exactComponentCount} components.
     * If no such sum is possible, an empty list is returned.
     */
    public static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents, int exactComponentCount) {
        final var sumsForTarget = sumsForTarget(target, sumComponents, list(), exactComponentCount);
        return sumsForTarget.stream().filter(e -> e.size() == exactComponentCount).collect(toList());
    }

    /**
     * TODO This method should not be used on its own, because it returns all sums, whose component size is equal or smaller than {@param exactComponentCount}.
     *
     * @param target
     * @param sumComponents
     * @param currentResult
     * @param exactComponentCount
     * @return
     */
    private static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents, List<Integer> currentResult, int exactComponentCount) {
        if (currentResult.size() >= exactComponentCount) {
            final var currentSum = currentResult.stream().reduce((a, b) -> a + b).orElse(0);
            if (currentSum == target) {
                return list(currentResult);
            } else {
                return list();
            }
        }
        // TODO This is an hack.
        final List<List<Integer>> nextResults = list();
        final var sum = currentResult.stream().reduce(Integer::sum).orElse(0);
        sumComponents.stream()
                .filter(c -> sum + c <= target)
                .map(c -> listWithValuesOf(currentResult).withAppended(c))
                .map(c -> sumsForTarget(target, sumComponents, c, exactComponentCount))
                .forEach(nextResults::addAll);
        if (nextResults.isEmpty()) {
            return list(currentResult);
        }
        return nextResults;
    }

    public static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents) {
        return sumsForTarget(target, sumComponents, list());
    }

    private static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents, List<Integer> currentResult) {
        final List<List<Integer>> nextResults = list();
        final var sum = currentResult.stream().reduce(Integer::sum).orElse(0);
        sumComponents.stream()
                .filter(c -> sum + c <= target)
                .map(c -> listWithValuesOf(currentResult).withAppended(c))
                .map(c -> sumsForTarget(target, sumComponents, c))
                .forEach(nextResults::addAll);
        if (nextResults.isEmpty()) {
            return list(currentResult);
        }
        return nextResults;
    }

    /**
     * @param dividend This is the dividend of the modulus.
     * @param divisor  This is the divisor of the modulus.
     * @return {@code dividend mod divisor} returns the reminder of the division of the dividend by the divisor.
     */
    public static int modulus(int dividend, int divisor) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (divisor < 0) {
                throw ExecutionException.execException("Negative divisor is not supported: " + divisor);
            }
            if (dividend < 0) {
                throw ExecutionException.execException("Negative dividend is not supported: " + dividend);
            }
        }
        return Math.floorMod(dividend, divisor);
    }

    public static int floorToInt(double arg) {
        return (int) Math.floor(arg);
    }

    public static int roundToInt(double arg) {
        return Math.toIntExact(Math.round(arg));
    }

    public static double intToDouble(int arg) {
        return Double.valueOf(arg);
    }

    public static float doubleToFloat(Double arg) {
        return arg.floatValue();
    }

    /**
     * TODO RENAME
     *
     * @return
     */
    public static boolean acceptable(double value, double target) {
        return acceptable(value, target, 0.1d);
    }

    /**
     * TODO RENAME
     *
     * <p>
     * This function calculates whether the error in a calculated {@code value},
     * is bigger than a given {@code maxDeviation} in percent.
     * </p>
     *
     * @param value        This is the value, that is computed.
     * @param target       This is the value, that is tried to be computed.
     * @param maxDeviation Allowed deviation from {@code target} in percent of the {@code target}.
     * @return Returns {@code true}, if the deviation of the {@code value} from the {@code target} is acceptable.
     */
    public static boolean acceptable(double value, double target, double maxDeviation) {
        return value < target * (1 + maxDeviation) && value > target * (1 - maxDeviation);
    }

    public static double distance(double a, double b) {
        if (a < b) {
            return Math.abs(b - a);
        } else {
            return Math.abs(a - b);
        }
    }

    public static double distance(int a, int b) {
        if (a < b) {
            return Math.abs(b - a);
        } else {
            return Math.abs(a - b);
        }
    }

    /**
     * @param arg The number for that the sign is calculated.
     * @return The sign represented by a number with the absolute of 1.
     */
    public static int sign(int arg) {
        if (arg < 0) {
            return -1;
        }
        return 1;
    }

    /**
     * @param arg The number for that the absolute is calculated.
     * @return Returns the absolute value of the given number.
     * This is the distance between 0 and the given number.
     */
    public static int absolute(int arg) {
        if (ENFORCING_UNIT_CONSISTENCY && arg == Integer.MIN_VALUE) {
            throw new IllegalArgumentException(Integer.MIN_VALUE + " is not supported.");
        }
        return Math.abs(arg);
    }

    /**
     * @param arg The number for that the absolute is calculated.
     * @return Returns the absolute value of the given number.
     * This is the distance between 0 and the given number.
     */
    public static double absolute(double arg) {
        return Math.abs(arg);
    }

    public static IntStream intervalClosed(int min, int max) {
        return IntStream.rangeClosed(min, max);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static double naturalLogarithm(double exponent) {
        return Math.log(exponent);
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    private MathUtils() {
        throw constructorIllegal();
    }

    public static boolean isEven(int arg) {
        return modulus(absolute(arg), 2) == 0;
    }
}
