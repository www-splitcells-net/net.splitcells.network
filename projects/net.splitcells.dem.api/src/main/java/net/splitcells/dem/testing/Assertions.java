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
import net.splitcells.dem.utils.ExecutionException;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.splitcells.dem.Dem.sleepABit;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class Assertions {
    private Assertions() {
        throw constructorIllegal();
    }

    public static <T> void assertComplies(T subject, Predicate<T> constraint, String description) {
        if (constraint.test(subject)) {
            throw ExecutionException.execException(description);

    public static <T> void requireEmpty(Optional<T> arg) {
        if (arg.isPresent()) {
            throw execException(tree("Optional has to be empty, but is not.")
                    .withProperty("Optional", arg.toString()));
        }
    }

    public static <T> void requireAbsenceOf(Optional<T> arg) {
        if (arg.isPresent()) {
            throw ExecutionException.execException("Optional content is required to be absent, but is present instead.");
        }
    }

    public static <T> void requirePresenceOf(Optional<T> arg) {
        if (arg.isEmpty()) {
            throw ExecutionException.execException("Optional content is required, but is not present instead.");
        }
    }

    public static <T> T requireNotNull(T arg) {
        if (arg == null) {
            throw ExecutionException.execException("The given variable is null, but should not be.");
        }
        return arg;
    }

    public static <T> void requireNotNull(T arg, String message) {
        if (arg == null) {
            throw ExecutionException.execException(message);
        }
    }

    public static <T> void requireNull(T arg, String message) {
        if (arg != null) {
            throw ExecutionException.execException(message);
        }
    }

    public static <T> void requireNull(T arg) {
        if (arg != null) {
            throw ExecutionException.execException("Argument is not allowed to be null, but is.");
        }
    }

    public static <T> void requireEquals(T a, T b) {
        if (a == null) {
            if (b == null) {
                return;
            }
            throw ExecutionException.execException("Arguments are required to be equal, but are not: first argument: "
                    + a + ", second argument: " + b);
        }
        if (!a.equals(b)) {
            // Assertj create a nice comparison report, also this report does not explicitly state, which is the
            // first argument and which is the second.
            org.assertj.core.api.Assertions.assertThat(a).isEqualTo(b);
        }
    }

    public static <T> void requireDistinct(T a, T b) {
        if (a.equals(b)) {
            throw ExecutionException.execException("Arguments are required to be equal, but are not: first argument: "
                    + a + ", second argument: " + b);
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

    public static void requireThrow(Class<? extends Throwable> expectedExceptionType, Runnable run) {
        try {
            run.run();
        } catch (Throwable th) {
            if (expectedExceptionType.isInstance(th)) {
                return;
            }
            throw ExecutionException.execException("Runnable should throw `" + expectedExceptionType + "` but did " +
                    "throw  `" + th.getClass() + "`.");
        }
        throw ExecutionException.execException("Runnable should throw `" + expectedExceptionType + "` but did not.");
    }

    public static void requireThrow(Runnable run) {
        try {
            run.run();
        } catch (Throwable th) {
            return;
        }
        throw ExecutionException.execException("Runnable should throw, but did not.");
    }

    public static void waitUntilRequirementIsTrue(long milliSecondsToWait, Supplier<Boolean> condition) {
        final var plannedEnd = Instant.now().plusMillis(milliSecondsToWait);
        var compliance = condition.get();
        while (true) {
            if (compliance) {
                return;
            }
            sleepABit();
            compliance = condition.get();
            if (Instant.now().isAfter(plannedEnd)) {
                throw ExecutionException.execException(tree("Condition is not met during wait time.")
                        .withProperty("milliSecondsToWait", "" + milliSecondsToWait)
                        .withProperty("condition", "" + condition));
            }
        }
    }
}
