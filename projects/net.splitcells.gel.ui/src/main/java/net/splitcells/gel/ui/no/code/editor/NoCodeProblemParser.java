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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParserBaseVisitor;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.ui.ProblemParser;
import net.splitcells.gel.ui.SolutionParameters;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class NoCodeProblemParser extends NoCodeDenParserBaseVisitor<Result<SolutionParameters, Perspective>> {
    public static Result<SolutionParameters, Perspective> parseNoCodeProblem(String arg) {
        final var lexer = new net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser(new CommonTokenStream(lexer));
        final List<Perspective> parsingErrors = list();
        parser.addErrorListener(new BaseErrorListener() {
            // Ensures, that error messages are not hidden.
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                    throws ParseCancellationException {
                if (offendingSymbol instanceof CommonToken) {
                    final var token = (CommonToken) offendingSymbol;
                    parsingErrors.add(perspective("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + token.toString(recognizer) + "`")
                            .withProperty("invalid token", "`" + token.getText() + "`")
                            .withProperty("error", msg));
                } else {
                    parsingErrors.add(perspective("Could not parse problem definition:")
                            .withProperty("line", "" + line)
                            .withProperty("column", "" + charPositionInLine)
                            .withProperty("invalid text", "`" + offendingSymbol.toString() + "`")
                            .withProperty("error", msg));
                }
            }
        });
        final var parsedProblem = new NoCodeProblemParser().visitSource_unit(parser.source_unit());
        parsedProblem.errorMessages().withAppended(parsingErrors);
        return parsedProblem;
    }

    private Result<SolutionParameters, Perspective> result = result();

    private NoCodeProblemParser() {

    }

    @Override
    public Result<SolutionParameters, Perspective> visitSource_unit(net.splitcells.dem.lang.perspective.no.code.antlr4.NoCodeDenParser.Source_unitContext sourceUnit) {
        return result;
    }
}
