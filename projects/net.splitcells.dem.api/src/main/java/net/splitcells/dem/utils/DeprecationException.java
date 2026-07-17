/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacy;

/**
 * Signals that a piece of code should not be used, also it is not removed yet.
 */
@JavaLegacy
public class DeprecationException extends RuntimeException {
    public static DeprecationException deprecationException() {
        return new DeprecationException();
    }

    private DeprecationException() {

    }
}
