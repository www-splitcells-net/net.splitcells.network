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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

@JavaLegacy
public class AntlrUtils {
    private AntlrUtils() {
        throw constructorIllegal();
    }

    public static BaseErrorListener baseErrorListener(List<Tree> parsingErrors) {
        return new BaseErrorListener() {
            // Ensures, that error messages are not hidden.
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                    throws ParseCancellationException {
                if (offendingSymbol instanceof CommonToken token) {
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + token.toString(recognizer) + "`")
                            .withProperty("invalid token", "`" + token.getText() + "`")
                            .withProperty("error", msg));
                } else {
                    parsingErrors.add(tree("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + offendingSymbol.toString() + "`")
                            .withProperty("error", msg));
                }
            }
        };
    }
}
