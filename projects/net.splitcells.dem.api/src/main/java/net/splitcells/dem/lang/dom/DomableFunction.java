/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang.dom;

import java.util.function.Function;

public interface DomableFunction<I, O> extends Domable, Function<I, O> {
}
