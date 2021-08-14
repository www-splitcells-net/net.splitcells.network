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

import static org.assertj.core.api.Assertions.assertThat;

public final class MathUtils {

    private MathUtils() {
        throw new ConstructorIllegal();
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
     * @return
     */
    public static boolean acceptable(double value, double target, double deviation) {
        return value < target + (1 + deviation) && value > target * (1 - deviation);
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

}
