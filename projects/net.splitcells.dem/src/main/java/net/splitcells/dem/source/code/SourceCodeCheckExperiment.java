/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.source.code;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.validator.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import java.io.IOException;
import java.nio.file.Paths;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.reflection.ClassesRelated.downCast;

public class SourceCodeCheckExperiment {
    public static void main(String... args) throws IOException {
        final var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new JavaParserTypeSolver(Paths.get("./src/main/java")));
        final var symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().getPostProcessors().add(
                new Validator() {
                    @Override
                    public void accept(Node node, ProblemReporter problemReporter) {
                        downCast(CompilationUnit.class, node).ifPresent(c -> {
                            if (c.getComment().isEmpty()) {
                                throw notImplementedYet();
                            }

                        });
                        throw notImplementedYet();
                    }
                }.postProcessor());
        CompilationUnit cu = StaticJavaParser.parse(Paths.get("./src/main/java/net/splitcells/dem/Dem.java"));
        System.out.println(cu);
    }
}
