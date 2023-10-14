package net.splitcells.gel.problem;

import net.splitcells.gel.ext.problem.antlr4.ProblemParser;
import net.splitcells.gel.ext.problem.antlr4.ProblemParserBaseVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class ProblemParser2 extends ProblemParserBaseVisitor<Problem> {
    public static Problem parseProblem(String arg) {
        final var lexer = new net.splitcells.gel.ext.problem.antlr4.ProblemLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.gel.ext.problem.antlr4.ProblemParser(new CommonTokenStream(lexer));
        return new ProblemParser2().visitSource_unit(parser.source_unit());
    }

    @Override
    public Problem visitSource_unit(ProblemParser.Source_unitContext ctx) {
        visitChildren(ctx);
        return null;
    }

}
