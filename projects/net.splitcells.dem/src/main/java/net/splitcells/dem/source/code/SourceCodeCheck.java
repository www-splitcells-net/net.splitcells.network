/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.source.code;

import net.splitcells.dem.resource.Files;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class SourceCodeCheck {
    public static void main(String... arg) {
        checkJavaSourceCodeProject(Path.of("../net.splitcells.dem.merger"));
    }

    private static void checkJavaSourceCodeProject(Path projectFolder) {
        Files.walk_recursively(projectFolder.resolve("src/main/java/"))
                .filter(Files::is_file)
                .forEach(SourceCodeCheck::checkJavaSourceCodeFile);
    }

    private static void checkJavaSourceCodeFile(Path file) {
        try {
            System.out.println("Checking file: " + file);
            final var lexer = new net.splitcells.dem.source.code.antlr.Java11Lexer
                    (CharStreams.fromFileName(file.toString()));
            final var parser = new net.splitcells.dem.source.code.antlr.Java11Parser
                    (new CommonTokenStream(lexer));
            // TODO REMOVE this, when this feature is implemented.
            //parser.addErrorListener(new DiagnosticErrorListener());
            //parser.setErrorHandler(new BailErrorStrategy());
            //parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
            parser.addErrorListener(new BaseErrorListener() {
                // Creates more easily understandable error message.
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                        throws ParseCancellationException {
                    if (offendingSymbol instanceof CommonToken) {
                        final var token = (CommonToken) offendingSymbol;
                        System.out.println("line " + line + ":" + charPositionInLine + " " + msg + ", " + token.toString(recognizer));
                    } else {
                        System.out.println("line " + line + ":" + charPositionInLine + " " + msg + ", " + offendingSymbol);
                    }
                    throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
                }
            });
            parser.source_unit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
