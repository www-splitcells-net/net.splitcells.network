/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.atom;

import net.splitcells.dem.utils.ExecutionException;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class Bools {
    private Bools() {
        throw constructorIllegal();
    }

    public static void requireNot(boolean arg) {
        if (arg) {
            throw ExecutionException.execException("Boolean should be false, but is not.");
        }
    }

    public static void require(boolean arg) {
        if (!arg) {
            throw ExecutionException.execException("Boolean should be true, but is not.");
        }
    }

    public static Bool bool(boolean arg) {
        return BoolI.bool(arg);
    }

    public static boolean parseBoolean(String parse) {
        return parse.toLowerCase().equals("true");
    }

    public static Bool truthful() {
        return BoolI.bool(true);
    }

    public static Bool untrue() {
        return BoolI.bool(false);
    }
}
