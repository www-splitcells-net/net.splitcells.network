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
