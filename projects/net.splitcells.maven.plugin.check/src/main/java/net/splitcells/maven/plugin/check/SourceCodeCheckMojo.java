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
package net.splitcells.maven.plugin.check;

import net.splitcells.dem.source.java.Java11Lexer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * <p>This goal should be executed in the validate phase.</p>
 */
@Mojo(name = "source-code-check")
public class SourceCodeCheckMojo extends AbstractMojo {
    /**
     * Matches a line, that only contains `@JavaLegacyArtifact`.
     */
    private static final Pattern CONTAINS_JAVA_LEGACY_ARTIFACT = Pattern.compile(".*[\\r\\n]@JavaLegacyArtifact[\\r\\n].*", Pattern.DOTALL);

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            for (final var sourceRoot : project.getCompileSourceRoots()) {
                // TODO The if is an hack.
                if (!sourceRoot.toString().contains("generated-sources")) {
                    try (final var walk = Files.walk(Path.of(sourceRoot.toString()))) {
                        walk.filter(Files::isRegularFile).forEach(this::checkJavaSourceCodeFile);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * `@JavaLegacyArtifact` is only allowed to mark code as code for external dependencies.
     * Such code has no checks and has to be isolated from the rest of the code.
     *
     * @param file This is the source code file to be checked.
     */
    private void checkJavaSourceCodeFile(Path file) {
        try {
            if (CONTAINS_JAVA_LEGACY_ARTIFACT.matcher(java.nio.file.Files.readString(file)).matches()) {
                return;
            }
            final var lexer = new net.splitcells.dem.source.java.Java11Lexer
                    (CharStreams.fromFileName(file.toString()));
            final var parser = new net.splitcells.dem.source.java.Java11Parser
                    (new CommonTokenStream(lexer));
            // TODO These error handler and listener creates errors, but maybe this is somehow useful?
            // parser.addErrorListener(new DiagnosticErrorListener());
            // parser.setErrorHandler(new BailErrorStrategy());
            parser.addErrorListener(new BaseErrorListener() {
                // Creates more easily understandable error message.
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                        throws ParseCancellationException {
                    if (offendingSymbol instanceof CommonToken) {
                        final var token = (CommonToken) offendingSymbol;
                        getLog().error("Invalid line; " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg + ", token = " + token.toString(recognizer));
                    } else {
                        getLog().error("Invalid line: " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg + ", offendingSymbol = offendingSymbol " + offendingSymbol);
                    }
                    throw new ParseCancellationException("Invalid line: " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg, e);
                }
            });
            parser.source_unit();
        } catch (Throwable e) {
            throw new RuntimeException("The source code file is invalid: " + file);
        }
    }
}
