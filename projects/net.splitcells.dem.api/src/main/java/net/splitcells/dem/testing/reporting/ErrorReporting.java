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
package net.splitcells.dem.testing.reporting;

import net.splitcells.dem.testing.need.NeedsCheck;
import net.splitcells.dem.utils.ExecutionException;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h2>Error Reporting API</h2>
 * <p>This API defines a single point of implicit error reporting.
 * This works like the {@link NeedsCheck} API, but instead of testing a given object,
 * the {@link Throwable} of an executed {@link Runnable} is processed instead.</p>
 * <p>Code of end users using this API would look like this and
 * therefore mainly avoids try-catch blocks:</p>
 * <pre><code>
 *     createCardDeck()
 *      .stream()
 *      .filter(card -> card.hasNumber())
 *      .reduce(0, (a,b) -> a.getNumber() + b.getNumber(), reportNumberSumError())
 *      .get(reportMissingSum());
 * </code></pre>
 * <h2>Tasks</h2>
 * <p>TODO One has to question if something like that, is really better than try-catch?
 * Consider this, when this API is used a lot more and thereby experience is acquired.
 * If this is removed in the future, you need to document the reasoning, so that the error is not done again.
 * One advantage of this API, is the fact,
 * that the code gets a bit more technology agnostic and thereby makes throw a tad bit more replaceable.</p>
 */
public class ErrorReporting {
    private ErrorReporting() {

    }

    public static void runWithReportedErrors(Runnable runnable, ErrorReporter reporter) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw reporter.apply(t);
        }
    }

    public static <T> T getWithReportedErrors(Supplier<T> runnable, ErrorReporter reporter) {
        try {
            return runnable.get();
        } catch (Throwable t) {
            throw reporter.apply(t);
        }
    }
}
