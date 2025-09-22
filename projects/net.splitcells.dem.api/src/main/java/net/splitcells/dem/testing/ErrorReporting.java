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

import net.splitcells.dem.testing.need.NeedsCheck;
import net.splitcells.dem.utils.ExecutionException;

import java.util.function.Function;

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
 */
public class ErrorReporting {
    private ErrorReporting() {

    }

    public static <T> void runWithReportedErrors(Runnable runnable, Function<Throwable, ExecutionException> reporter) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw reporter.apply(t);
        }
    }
}
