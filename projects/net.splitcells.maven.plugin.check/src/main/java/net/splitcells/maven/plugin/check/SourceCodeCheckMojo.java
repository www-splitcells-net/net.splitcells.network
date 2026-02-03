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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.nio.file.Files.isDirectory;
import static java.util.stream.Collectors.toList;

/**
 * <p>This goal should be executed in the validate phase.</p>
 */
@Mojo(name = "source-code-check")
public class SourceCodeCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;
    @Parameter(defaultValue = "${exector.parallism.use}", required = false, readonly = true)
    private boolean useExectorParallism = true;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            final var enabled = System.getenv("net_splitcells_maven_plugin_check_enabled");
            if (enabled != null && !enabled.equals("1")) {
                getLog().warn("Source code checking is disabled by the environment variable `net_splitcells_maven_plugin_check_enabled`.");
                return;
            }
            for (final var sourceRoot : project.getCompileSourceRoots()) {
                // TODO The if is an hack.
                if (sourceRoot.toString().contains("generated-sources")) {
                    getLog().info("Ignoring the the source folder, as it is generated: " + sourceRoot);
                } else {
                    final var rootPath = Path.of(sourceRoot.toString());
                    if (isDirectory(rootPath)) {
                        List<Throwable> errors = new ArrayList<>();
                        try (final var walk = Files.walk(rootPath)) {
                            // Collect is done, because the parallel Stream of walk itself is not as good as a dedicated parallel processing.
                            final var files = walk.parallel().toList();
                            if (useExectorParallism) {
                                try (final var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
                                    files.stream().filter(Files::isRegularFile).forEach(f -> {
                                        executor.submit(() -> {
                                            try {
                                                checkJavaSourceCodeFile(f);
                                            } catch (Throwable th) {
                                                errors.add(th);
                                                th.printStackTrace();
                                            }
                                        });
                                    });
                                    try {
                                        executor.shutdown();
                                        executor.awaitTermination(1, TimeUnit.MINUTES);
                                        if (!errors.isEmpty()) {
                                            throw new MojoExecutionException("The source code syntax is invalid. See logging of throwable for the errors.");
                                        }
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException("Source code checking takes too much time.", e);
                                    }
                                }
                            } else {
                                getLog().warn("${exector.parallism.use} is set to false. This is the about 4 times slower parallelism mechanism.");
                                files.parallelStream().filter(Files::isRegularFile).forEach(this::checkJavaSourceCodeFile);
                            }
                        }
                    } else {
                        getLog().warn("Given source folder path is not an actual folder: " + rootPath);
                    }
                }
            }
        } catch (IOException e) {
            // This is logged, as otherwise the stack trace is not visible.
            getLog().error("File error during check.", e);
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
            final var fileContent = java.nio.file.Files.readString(file);
            if (fileContent.contains("@JavaLegacy")) {
                getLog().debug("Ignoring Java legacy file: " + file);
                return;
            }
            getLog().debug("Checking file: " + file);
            final var lexer = new net.splitcells.dem.source.java.Java11Lexer(CharStreams.fromString(fileContent));
            final var parser = new net.splitcells.dem.source.java.Java11Parser(new CommonTokenStream(lexer));
            // TODO These error handler and listener creates errors, but maybe this is somehow useful?
            // parser.addErrorListener(new DiagnosticErrorListener());
            // parser.setErrorHandler(new BailErrorStrategy());
            parser.addErrorListener(new BaseErrorListener() {
                // Creates more easily understandable error message.
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                        throws ParseCancellationException {
                    if (offendingSymbol instanceof CommonToken token) {
                        getLog().error("Invalid line; " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg + ", token = " + token.toString(recognizer));
                    } else {
                        getLog().error("Invalid line: " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg + ", offendingSymbol = offendingSymbol " + offendingSymbol);
                    }
                    throw new ParseCancellationException("Invalid line: " + line + ", charPositionInLine = " + charPositionInLine + ", msg = " + msg, e);
                }
            });
            parser.source_unit();
        } catch (Throwable e) {
            // This is logged, as otherwise the stack trace is not visible.
            getLog().error("The source code file is invalid: " + file, e);
            throw new RuntimeException("The source code file is invalid: " + file, e);
        }
    }
}
