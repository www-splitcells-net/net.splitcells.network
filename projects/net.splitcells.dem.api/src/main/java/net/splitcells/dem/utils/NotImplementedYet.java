/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.lang.annotations.JavaLegacy;

public final class NotImplementedYet extends RuntimeException {

    public static final String TODO_NOT_IMPLEMENTED_YET = "TODO-Not-implemented-yet";

    public static void throwNotImplementedYet() {
        throw new NotImplementedYet();
    }

    /**
     * This method is needed, in order to combine it with a throw statement,
     * which is helpful regarding variable scopes in function bodies,
     * where this exception is used.
     * Using {@link #throwNotImplementedYet()} is not possible in such situation.
     *
     * @return
     */
    public static NotImplementedYet notImplementedYet() {
        return new NotImplementedYet();
    }

    public static NotImplementedYet notImplementedYet(String message) {
        return new NotImplementedYet(message);
    }

    private NotImplementedYet() {

    }

    @JavaLegacy
    private NotImplementedYet(String message) {
        super(message);
    }

}
