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
package net.splitcells.dem.lang;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class CommonMarkUtils {
    private CommonMarkUtils() {
        throw constructorIllegal();
    }

    public static String joinDocuments(String a, String b) {
        final int newLinesAtEnd = newLinesAtEnd(a) + newLinesAtStart(b);
        final var filler = range(0, newLinesAtEnd)
                .mapToObj(i -> "\n")
                .reduce((ia, ib) -> ia + ib)
                .orElse("");
        return a + filler + b;
    }

    protected static int newLinesAtEnd(String arg) {
        if (arg.length() == 0) {
            return 0;
        }
        int counter = 0;
        for (int i = arg.length() - 1 ; i >= 0 ; --i) {
            if (arg.charAt(i) == '\n') {
                ++counter;
            } else {
                break;
            }
        }
        return counter;
    }

    protected static int newLinesAtStart(String arg) {
        if (arg.length() == 0) {
            return 0;
        }
        int counter = 0;
        for (int i = 0 ; i < arg.length() ; ++i) {
            if (arg.charAt(i) == '\n') {
                ++counter;
            } else {
                break;
            }
        }
        return counter;
    }
}
