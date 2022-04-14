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
package net.splitcells.dem.utils;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static org.assertj.core.api.Assertions.assertThat;

public final class MathUtils {

    public static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents) {
        return sumsForTarget(target, sumComponents, list());
    }

    private static List<List<Integer>> sumsForTarget(int target, List<Integer> sumComponents
            , List<Integer> currentResult) {
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

    public static int modulus(int dividend, int divisor) {
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

    public static float doubleToFloat(double arg) {
        return Double.valueOf(arg).floatValue();
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
        return value < target + (1 + maxDeviation) && value > target * (1 - maxDeviation);
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

    private MathUtils() {
        throw constructorIllegal();
    }
}
