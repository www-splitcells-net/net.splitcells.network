package net.splitcells.dem.source;/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */

import org.antlr.v4.runtime.ParserRuleContext;

public class SourceUtils {
    private SourceUtils() {
        throw new RuntimeException();
    }

    public static ParserRuleContext root(ParserRuleContext arg) {
        if (arg.getParent() == null) {
            return arg;
        }
        return root(arg.getParent());
    }

    public static int getLine(ParserRuleContext arg) {
        return arg.getStart().getLine();
    }
}
