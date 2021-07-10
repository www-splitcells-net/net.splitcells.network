package net.splitcells.dem.source.code;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.validator.ProblemReporter;
import com.github.javaparser.ast.validator.TypedValidator;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import java.io.IOException;
import java.nio.file.Paths;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class SourceCodeCheckExperiment {
    public static void main(String... args) throws IOException {
        final var typeSolver = new CombinedTypeSolver();
        typeSolver.add(new JavaParserTypeSolver(Paths.get("./src/main/java")));
        final var symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().getPostProcessors().add(
                new TypedValidator<Node>() {

                    @Override
                    public void accept(Node node, ProblemReporter problemReporter) {
                        throw notImplementedYet();
                    }
                }.postProcessor());
        CompilationUnit cu = StaticJavaParser.parse(Paths.get("./src/main/java/net/splitcells/dem/Dem.java"));
        System.out.println(cu);
    }
}
