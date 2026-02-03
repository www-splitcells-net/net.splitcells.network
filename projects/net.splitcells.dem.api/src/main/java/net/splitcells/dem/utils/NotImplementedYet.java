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
