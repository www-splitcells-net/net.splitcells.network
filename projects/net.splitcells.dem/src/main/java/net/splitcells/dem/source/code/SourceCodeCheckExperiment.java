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

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.validator.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import java.io.IOException;
import java.nio.file.Paths;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.reflection.ClassesRelated.downCast;

@JavaLegacyArtifact
public class SourceCodeCheckExperiment {
    public static void main(String... args) throws IOException {
        final var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new JavaParserTypeSolver(Paths.get("./src/main/java")));
        final var symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        // TODO REMOVE When #10 Is one.
        StaticJavaParser.getConfiguration().getPostProcessors().add(new TreeVisitorValidator(new Validator() {
            @Override
            public void accept(Node node, ProblemReporter problemReporter) {
                System.out.println(node.getClass());
                final var accepted = downCast(CompilationUnit.class, node).map(cu -> {
                    if (cu.getComment().isEmpty()) {
                        throw notImplementedYet();
                    }
                    cu.getChildNodes().forEach(c -> {
                        if (downCast(PackageDeclaration.class, c).isPresent()
                                || downCast(ImportDeclaration.class, c).isPresent()
                                || downCast(ClassOrInterfaceDeclaration.class, c).isPresent()) {
                            return;
                        }
                        throw new IllegalArgumentException(c.getClass().toString());
                    });
                    return true;
                }).orElse(false);
                if (accepted) {
                    return;
                }
                throw new IllegalArgumentException(node.getClass().toString());
            }
        }).postProcessor());
        CompilationUnit cu = StaticJavaParser.parse(Paths.get("./src/main/java/net/splitcells/dem/Dem.java"));
        System.out.println(cu);
    }
}
