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

import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.utils.ConstructorIllegal;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.splitcells.dem.lang.tree.TreeI.perspective;
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

    public static <T> void requirePresenceOf(Optional<T> arg) {
        if (arg.isEmpty()) {
            throw executionException("Optional content is required, but not present.");
        }
    }

    public static <T> T requireNotNull(T arg) {
        if (arg == null) {
            throw executionException("The given variable is null, but should not be.");
        }
        return arg;
    }

    public static <T> void requireNotNull(T arg, String message) {
        if (arg == null) {
            throw executionException(message);
        }
    }

    public static <T> void requireNull(T arg, String message) {
        if (arg != null) {
            throw executionException(message);
        }
    }

    public static <T> void requireNull(T arg) {
        if (arg != null) {
            throw executionException("Argument is not allowed to be null, but is.");
        }
    }

    public static <T> void requireEquals(T a, T b) {
        if (!a.equals(b)) {
            throw executionException("Arguments are required to be equal, but are not: first argument: " + a + ", second argument: " + b);
        }
    }

    @JavaLegacyBody
    public static void requireIllegalDefaultConstructor(Class<?> clazz) {
        final java.lang.reflect.Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        final var constructorWasAccessible = constructor.isAccessible();
        try {
            if (!constructorWasAccessible) {
                constructor.setAccessible(true);
            }
            constructor.newInstance();
        } catch (ConstructorIllegal e) {
            throw e;
        } catch (Throwable e) {
            if (e.getCause() instanceof ConstructorIllegal) {
                return;
            }
            throw new RuntimeException(e);
        } finally {
            if (!constructorWasAccessible) {
                constructor.setAccessible(false);
            }
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

    public static void waitUntilRequirementIsTrue(long milliSecondsToWait, Supplier<Boolean> condition) {
        final var plannedEnd = Instant.now().plusMillis(milliSecondsToWait);
        var compliance = condition.get();
        while (true) {
            if (compliance) {
                return;
            }
            compliance = condition.get();
            if (Instant.now().isAfter(plannedEnd)) {
                throw executionException(perspective("Condition is not met during wait time.")
                        .withProperty("milliSecondsToWait", "" + milliSecondsToWait)
                        .withProperty("condition", "" + condition));
            }
        }
    }
}
