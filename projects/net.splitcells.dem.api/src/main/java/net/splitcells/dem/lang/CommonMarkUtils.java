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
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class CommonMarkUtils {
    private CommonMarkUtils() {
        throw constructorIllegal();
    }

    /**
     * TODO IDEA Consider trimming new lines blocks bigger than 2 to new line blocks of length 2 at the end of a and start of a.
     * This avoids too many new line symbols between the content of a and b.
     *
     * @param a
     * @param b
     * @return
     */
    public static String joinDocuments(String a, String b) {
        final int newLinesPresent = newLinesAtEnd(a) + newLinesAtStart(b);
        final int missingNewLines;
        if (newLinesPresent == 0) {
            missingNewLines = 2;
        } else if (newLinesPresent == 1) {
            missingNewLines = 1;
        } else if (newLinesPresent > 1) {
            missingNewLines = 0;
        } else {
            throw execException(tree("")
                    .withProperty("a", a)
                    .withProperty("b", b));
        }
        final var filler = range(0, missingNewLines)
                .mapToObj(i -> "\n")
                .reduce((ia, ib) -> ia + ib)
                .orElse("");
        return a + filler + b;
    }

    /**
     *
     * @param a Into this object the content of b will be written, in order to create the joined document.
     * @param b This is CommonMark to be added to a.
     * @return This is the argument a.
     */
    public static StringBuilder joinDocuments(StringBuilder a, StringBuilder b) {
        final int newLinesPresent = newLinesAtEnd(a) + newLinesAtStart(b);
        final int missingNewLines;
        if (newLinesPresent == 0) {
            missingNewLines = 2;
        } else if (newLinesPresent == 1) {
            missingNewLines = 1;
        } else if (newLinesPresent > 1) {
            missingNewLines = 0;
        } else {
            throw execException(tree("")
                    .withProperty("a", a.toString())
                    .withProperty("b", b.toString()));
        }
        final var filler = range(0, missingNewLines)
                .mapToObj(i -> "\n")
                .reduce((ia, ib) -> ia + ib)
                .orElse("");
        a.append(filler);
        a.append(b);
        return a;
    }

    protected static int newLinesAtEnd(CharSequence arg) {
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

    protected static int newLinesAtStart(CharSequence arg) {
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
