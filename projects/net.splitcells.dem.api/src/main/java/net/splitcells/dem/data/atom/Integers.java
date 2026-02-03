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
package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.regex.Pattern;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class Integers {

    private static final Pattern NUMBER = Pattern.compile("[0-9]+");

    private Integers() {
        throw constructorIllegal();
    }

    @JavaLegacy
    public static int parse(String arg) {
        return java.lang.Integer.parseInt(arg);
    }

    @JavaLegacy
    public static boolean isEven(Integer arg) {
        return arg % 2 == 0;
    }

    @JavaLegacy
    public static boolean isNumber(String arg) {
        return NUMBER.matcher(arg).matches();
    }

    public static void requireEqualInts(int a, int b) {
        if (a != b) {
            throw ExecutionException.execException("Ints should be equals, but are not: " + a + ", " + b);
        }
    }

    public static void requireNotNegative(int a) {
        if (a < 0) {
            throw ExecutionException.execException("Int mustn't be positive, but is: " + a);
        }
    }
}
