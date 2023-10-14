package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.ext.problem.antlr4.ProblemParserBaseVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class ProblemParser extends ProblemParserBaseVisitor<Problem> {

    private String name;

    private Database demands;
    private Database supplies;
    private Constraint constraints;

    public static Problem parseProblem(String arg) {
        final var lexer = new net.splitcells.gel.ext.problem.antlr4.ProblemLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.gel.ext.problem.antlr4.ProblemParser(new CommonTokenStream(lexer));
        return new ProblemParser().visitSource_unit(parser.source_unit());
    }

    @Override
    public Problem visitSource_unit(net.splitcells.gel.ext.problem.antlr4.ProblemParser.Source_unitContext ctx) {
        visitChildren(ctx);
        final var assignments = assignments(name, demands, supplies);
        return null;
    }

    @Override
    public Problem visitVariable_definition(net.splitcells.gel.ext.problem.antlr4.ProblemParser.Variable_definitionContext ctx) {
        final var ctxName = ctx.Name().getText();
        if (ctxName.equals("name")) {
            if (name != null) {
                throw executionException("Names are not allowed to be defined multiple times.");
            }
            name = ctxName;
        } else if (ctxName.equals("demands")) {
            if (demands != null) {
                throw executionException("Demands are not allowed to be defined multiple times.");
            }
            final List<Attribute<? extends Object>> demandAttributes = list();
            final var firstDemandAttribute = ctx.map().variable_definition();
            if (firstDemandAttribute != null) {
                demandAttributes.add(
                        parseAttribute(firstDemandAttribute.Name().getText()
                                , firstDemandAttribute.function_call().Name().getText()));
            }
            final var additionalDemandAttributes = ctx.map().statement_reversed();
            additionalDemandAttributes.forEach(da -> demandAttributes
                    .add(parseAttribute(da.variable_definition().Name().getText()
                            , da.variable_definition().function_call().Name().getText())));
            demands = database(demandAttributes);
        } else if (ctxName.equals("supplies")) {
            if (supplies != null) {
                throw executionException("Supplies are not allowed to be defined multiple times.");
            }
            final List<Attribute<? extends Object>> supplyAttributes = list();
            final var firstSupplyAttribute = ctx.map().variable_definition();
            if (firstSupplyAttribute != null) {
                supplyAttributes.add(
                        parseAttribute(firstSupplyAttribute.Name().getText()
                                , firstSupplyAttribute.function_call().Name().getText()));
            }
            final var additionalSupplyAttributes = ctx.map().statement_reversed();
            additionalSupplyAttributes.forEach(sa -> supplyAttributes
                    .add(parseAttribute(sa.variable_definition().Name().getText()
                            , sa.variable_definition().function_call().Name().getText())));
            supplies = database(supplyAttributes);
        } else if (ctxName.equals("constraints")) {
            constraints = parseConstraint(ctx);
        }
        return null;
    }

    private Constraint parseConstraint(net.splitcells.gel.ext.problem.antlr4.ProblemParser.Variable_definitionContext variableDefinition) {
        final var constraintName = variableDefinition.function_call().Name().getText();
        if (!variableDefinition.function_call().access().isEmpty()) {
            throw executionException("Function chaining is not supported for constraint definition yet.");
        }
        if (constraintName.equals("forAll")) {
            if (variableDefinition.function_call().call_arguments().call_arguments_element() != null) {
                throw executionException("ForAll does not support arguments");
            }
            return forAll();
        } else if (constraintName.equals("forEach")) {
            if (variableDefinition.function_call().call_arguments().call_arguments_element() != null
                    && variableDefinition.function_call().call_arguments().call_arguments_next().isEmpty()) {
                if (!variableDefinition.function_call().call_arguments().call_arguments_element().function_call().isEmpty()) {
                    throw executionException("Function call argument are not supported for forEach constraint.");
                }
                final var attributeName = variableDefinition.function_call()
                        .call_arguments()
                        .call_arguments_element()
                        .Name()
                        .getText();
                final var demandAttributeMatches = demands.headerView().stream()
                        .filter(da -> da.name().equals(attributeName))
                        .collect(toList());
                final var supplyAttributeMatches = supplies.headerView().stream()
                        .filter(sa -> sa.name().equals(attributeName))
                        .collect(toList());
                requireEquals(demandAttributeMatches.size() + supplyAttributeMatches.size(), 1);
                final Attribute<? extends Object> matchedAttribute;
                if (demandAttributeMatches.hasElements()) {
                    matchedAttribute = demandAttributeMatches.get(0);
                } else if (supplyAttributeMatches.hasElements()) {
                    matchedAttribute = supplyAttributeMatches.get(0);
                } else {
                    throw executionException("Invalid program state.");
                }
                return forEach(matchedAttribute);
            }
            if (!variableDefinition.function_call().call_arguments().call_arguments_next().isEmpty()) {
                throw executionException("ForEach does not support multiple arguments.");
            }
            throw executionException("Invalid program state.");
        } else {
            throw executionException("Unknown constraint name: " + constraintName);
        }
    }

    private Attribute<? extends Object> parseAttribute(String name, String type) {
        if (type.equals("int")) {
            return attribute(Integer.class, name);
        } else if (type.equals("float")) {
            return attribute(Float.class, name);
        } else if (type.equals("string")) {
            return attribute(String.class, name);
        } else {
            throw executionException(type);
        }
    }
}
