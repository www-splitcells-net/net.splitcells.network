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
package net.splitcells.dem.testing;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class Assertions {
    private Assertions() {
        throw constructorIllegal();
    }

    public static <T> void assertComplies(T subject, Predicate<T> constraint, String description) {
        if (constraint.test(subject)) {
            throw executionException(description);
        }
    }

    public static <T> void requireNotNull(T arg) {
        if (arg == null) {
            throw executionException("The given variable is null, but should not be.");
        }
    }

    public static <T> void requireNotNull(T arg, String message) {
        if (arg == null) {
            throw executionException(message);
        }
    }

    public static <T> void requireEquals(T a, T b) {
        if (!a.equals(b)) {
            throw executionException("Arguments are required to be equal, but are not: " + a + ", " + b);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedExceptionType, Runnable run) {
        try {
            run.run();
        } catch (Throwable th) {
            if (expectedExceptionType.isInstance(th)) {
                return;
            }
            throw executionException("Runnable should throw `" + expectedExceptionType + "` but did throw  `" + th.getClass() + "`.");
        }
        throw executionException("Runnable should throw `" + expectedExceptionType + "` but did not.");
    }
}
