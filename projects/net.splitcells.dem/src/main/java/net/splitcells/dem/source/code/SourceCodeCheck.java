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
package net.splitcells.dem.source.code;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.source.code.antlr.Java11Lexer;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@JavaLegacyArtifact
public class SourceCodeCheck {
    public static void main(String... arg) {
        checkJavaSourceCodeFile(Path.of(
                "../net.splitcells.dem.api/src/main/java/net/splitcells/dem/lang/tree/TreeTest.java"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.website.server"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.gel.ui"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.dem.api"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.gel.core"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.network.worker.via.java"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.gel.sheath"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.dem.merger"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.dem.core"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.dem"));
        checkJavaSourceCodeProject(Path.of("../net.splitcells.cin"));
        Files.walkDirectChildren(Path.of("..")).forEach(SourceCodeCheck::checkJavaSourceCodeProject);
        checkJavaSourceCodeProject(Path.of("../.."));
    }

    private static void checkJavaSourceCodeProject(Path projectFolder) {
        if (Files.isDirectory(projectFolder.resolve("src/main/java/"))
                && !projectFolder.getFileName().toString().equals("net.splitcells.maven.plugin.resource.list")) {
            Files.walk_recursively(projectFolder.resolve("src/main/java/"))
                    .filter(Files::is_file)
                    .forEach(SourceCodeCheck::checkJavaSourceCodeFile);
        }
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
                    throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg, e);
                }
            });
            parser.source_unit();
        } catch (Throwable e) {
            System.out.println("Fallback output:");
            try {
                final Java11Lexer lexer = new Java11Lexer
                        (CharStreams.fromFileName(file.toString()));
                // System.out.println(lexer.getAllTokens()); TODO The output is too big.
            } catch (IOException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);
        }
    }
}
