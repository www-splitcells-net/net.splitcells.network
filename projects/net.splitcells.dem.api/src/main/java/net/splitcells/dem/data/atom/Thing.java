/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.utils.ExecutionException.execException;

public interface Thing {
    default <T> void requireEqualityTo(T arg) {
        if (!equals(arg)) {
            throw ExecutionException.execException("Should be equal, but are not: " + this + ", " + arg);
        }
    }

    /**
     * This method is used for lambda references.
     *
     * @param a   a
     * @param b   b
     * @param <T> T
     * @return return
     */
    static <T> boolean equals(T a, T b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    @JavaLegacy
    static int hashCode(Object... args) {
        return java.util.Objects.hash(args);
    }

    static <T> T instance(Class<? extends T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            throw execException(t);
        }
    }

    public static String toStringOrNull(Object arg) {
        if (arg == null) {
            return null;
        }
        return arg.toString();
    }
}
