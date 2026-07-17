/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.reporting;

import net.splitcells.dem.utils.ExecutionException;

import java.util.function.Function;

@FunctionalInterface
public interface ErrorReporter extends Function<Throwable, ExecutionException> {
}
