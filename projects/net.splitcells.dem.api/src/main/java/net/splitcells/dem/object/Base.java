/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.utils.ExecutionException.execException;

public interface Base {
    default <T> void requireEqualsTo(T arg) {
        if (!this.equals(arg)) {
            throw ExecutionException.execException("This should be equals to arg, but is not: this is `" + this + "` and arg is `" + arg + "`.");
        }
    }
}
