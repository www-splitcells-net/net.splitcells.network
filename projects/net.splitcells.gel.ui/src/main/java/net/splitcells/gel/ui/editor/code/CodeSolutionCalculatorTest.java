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
package net.splitcells.gel.ui.editor.code;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.editor.lang.AttributeDescription;
import net.splitcells.gel.editor.lang.ReferenceDescription;
import net.splitcells.gel.ui.GelUiCell;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.gel.ui.editor.SolutionCalculator;
import net.splitcells.website.server.projects.extension.impls.ColloquiumPlanningDemandsTestData;
import net.splitcells.website.server.projects.extension.impls.ColloquiumPlanningSuppliesTestData;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Optional;

import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.atom.Bools.truthful;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.dem.testing.Assertions.*;
import static net.splitcells.dem.utils.StringUtils.requireNonEmptyString;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.FOR_ALL_VALUE_COMBINATIONS_NAME;
import static net.splitcells.gel.ui.editor.code.CodeEditorLangParser.codeEditorLangParsing;
import static net.splitcells.gel.ui.editor.code.CodeSolutionEditorParser.PROBLEM_DEFINITION;
import static net.splitcells.gel.ui.editor.code.CodeSolutionEditorParser.codeSolutionEditorParser;
import static net.splitcells.gel.ui.editor.SolutionCalculator.*;
import static net.splitcells.website.server.client.HtmlClients.htmlClient;
import static net.splitcells.website.server.processor.Request.request;

public class CodeSolutionCalculatorTest {
    public static final Runnable TEST_OPTIMIZATION_GUI = () -> {
        try (final var browser = htmlClient()) {
            try (final var tab = browser.openTab("/net/splitcells/gel/ui/editor/code/index.html")) {
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-errors").textContent());
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-solution").textContent());
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-solution-rating").textContent());
                tab.elementByClass("net-splitcells-website-pop-up-confirmation-button").click();
                tab.elementById("net-splitcells-gel-ui-calculate-solution-form-submit-1").click();
                waitUntilRequirementIsTrue(1000L * 60, () -> !tab.elementById("net-splitcells-gel-editor-form-solution").value().isEmpty());
                requireEquals("", tab.elementById("net-splitcells-gel-editor-form-errors").textContent());
                requireNonEmptyString(tab.elementById("net-splitcells-gel-editor-form-solution-rating").textContent());
            }
        }
    };

    /**
     * <p>TODO This test does not work on Codeberg.</p>
     * <p>TODO This test should be made deterministic.</p>
     * <p>TODO Additionally a random tests with probabilistic successes could be supported as well.
     * It should be stored in the network log, how often the test failed or succeeded yet.
     * Another job should check the ratio between failed tests and succeeded ones.</p>
     */
    @IntegrationTest
    @DisabledTest
    public void testOptimization() {
        process(TEST_OPTIMIZATION_GUI, GelUiCell.class).requireErrorFree();
    }

    @UnitTest
    public void testMinimalProcess() {
        final var testData = """
                demands={a=integer();b=string()};
                supplies={c=integer()};
                constraints=forEach(a).then(hasSize(2));
                constraints().forEach(b).then(allSame(c));
                name="testParseProblem";""";
        final var testResult = solutionCalculator().process(request(SolutionCalculator.PATH
                , tree("").withProperty(PROBLEM_DEFINITION,
                        tree(testData))));
        requireEquals(testResult.data()
                        .propertyInstance(SolutionCalculator.SOLUTION)
                        .orElseThrow()
                        .child(0)
                        .name()
                , "a,b,c,argumentation\n");
    }

    @UnitTest
    public void testMinimalProblem() {
        final var testData = """
                demands={a=integer();b=string()};
                supplies={c=integer()};
                constraints=forEach(a).forEach(b).then(hasSize(2));
                constraints().forEach(b).then(allSame(c));
                name="testParseProblem";""";
        final var testResult = codeSolutionEditorParser()
                .apply(request(trail(), tree("").withProperty(PROBLEM_DEFINITION, testData)));
        testResult.requireWorking();
        final var solution = testResult.optionalValue().orElseThrow().solution().orElseThrow();
        solution.headerView2().requireContentsOf((a, b) -> a.equalContentTo(b)
                , integerAttribute("a")
                , stringAttribute("b")
                , integerAttribute("c"));
        solution.constraint().readQuery().forAll(solution.attributeByName("a")).forAll(solution.attributeByName("b")).then(hasSize(2));
    }

    @UnitTest
    public void testGrammarAssumption() {
        final var testData = "demands=forAll().then();";
        final var lexer = new net.splitcells.dem.source.den.DenLexer(CharStreams.fromString(testData));
        final var parser = new net.splitcells.dem.source.den.DenParser(new CommonTokenStream(lexer));
        final var testResult = parser.source_unit();
        requireEquals(testResult.statement().get(0).variable_definition().Name().getText()
                , "demands");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().Name().getText()
                , "forAll");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().access().Name().getText()
                , "then");
    }

    @UnitTest
    public void testOutputFormat() {
        final var resultData = codeEditorLangParsing(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/editor/code/examples/school-course-scheduling-problem.txt")).requireWorking()
                .optionalValue().orElseThrow();
        resultData.columnAttributesForOutputFormat().requireEquality(list(referenceDescription("roomNumber", AttributeDescription.class, sourceCodeQuote("", "", 0)))
                , ReferenceDescription.equalMeaning());
        resultData.rowAttributesForOutputFormat().requireEquality(list(referenceDescription("date", AttributeDescription.class, sourceCodeQuote("", "", -1))
                        , referenceDescription("shift", AttributeDescription.class, sourceCodeQuote("", "", -1)))
                , ReferenceDescription.equalMeaning());
    }

    @UnitTest
    public void testParseProblem() {
        final var testData = """
                demands={a=int();b=string()};
                supplies={c=integer()};
                constraints=forEach(a).then(hasSize(2));
                constraints().forEach(b).then(allSame(c));
                name="testParseProblem";""";
        final var testSubject = codeSolutionEditorParser()
                .apply(request(trail(), tree("").withProperty(PROBLEM_DEFINITION, testData)))
                .requiredValue()
                .solution()
                .orElseThrow();
        final var forEachA = testSubject.constraint().child(0);
        requireEquals(forEachA.type(), ForAll.class);
        requirePresenceOf(forEachA.arguments().get(0).toTree().pathOfValueTree(
                "rater-based-on-grouping"
                , "grouping"
                , "for-all-attribute-values"
                , "attribute"
                , "Attribute"
                , "name"
                , "a"));
        final var thenHasSize2 = forEachA.child(0);
        requireEquals(thenHasSize2.type(), Then.class);
        requirePresenceOf(thenHasSize2.arguments().get(0).toTree().pathOfValueTree(
                "has-size"
                , "target-size"
                , "2"));
        final var forEachB = testSubject.constraint().child(1);
        requireEquals(forEachB.type(), ForAll.class);
        requirePresenceOf(forEachB.arguments().get(0).toTree().pathOfValueTree(
                "rater-based-on-grouping"
                , "grouping"
                , "for-all-attribute-values"
                , "attribute"
                , "Attribute"
                , "name"
                , "b"));
        final var thenAllSameC = forEachB.child(0);
        requireEquals(thenAllSameC.type(), Then.class);
        requirePresenceOf(thenAllSameC.arguments().get(0).toTree().pathOfValueTree(
                "all-same"
                , "attribute"
                , "Attribute"
                , "name"
                , "c"));
    }

    @UnitTest
    public void testParseProblemWithForAllCombinationsOf() {
        final var testData = """
                demands = {a = int(); b = string()};
                supplies = {c = integer()};
                constraints = forAllCombinationsOf(a, b, c);
                name = "testParseProblem";""";
        final var testSubject = codeSolutionEditorParser()
                .apply(request(trail(), tree("").withProperty(PROBLEM_DEFINITION, testData)))
                .requiredValue()
                .solution()
                .orElseThrow();
        final var forAllCombinationsOf = testSubject.constraint().child(0);
        requireEquals(forAllCombinationsOf.type(), ForAll.class);
        requirePresenceOf(forAllCombinationsOf.arguments().get(0).toTree().pathOfValueTree(
                "rater-based-on-grouping"
                , "grouping"
                , FOR_ALL_VALUE_COMBINATIONS_NAME
                , "attributes"
                , "Attribute"
                , "name"
                , "a"));
        final var attributes = forAllCombinationsOf.arguments().get(0).toTree()
                .subtreeByName("grouping"
                        , FOR_ALL_VALUE_COMBINATIONS_NAME
                        , "attributes");
        requireEquals(attributes.child(1).subtreeByName("name", "b").name(), "b");
        requireEquals(attributes.child(2).subtreeByName("name", "c").name(), "c");
    }

    @UnitTest
    public void testInvalidDemandAttribute() {
        final var testData = """
                demands={a=invalid_attribute()};
                supplies={};
                constraints=forEach(a);
                name="testInvalidDemandAttribute";""";
        codeSolutionEditorParser()
                .apply(request(trail(), tree("").withProperty(PROBLEM_DEFINITION, testData)))
                .errorMessages().requireAnyContent();
    }

    @UnitTest
    public void testInvalidSupplyAttribute() {
        final var testData = """
                demands={};
                supplies={a=invalid_attribute()};
                constraints=forEach(a);
                name="testInvalidDemandAttribute";""";
        codeSolutionEditorParser()
                .apply(request(trail(), tree("").withProperty(PROBLEM_DEFINITION, testData)))
                .errorMessages().requireAnyContent();
    }

    @CapabilityTest
    public void testDemonstrationExample() {
        Dem.process(() -> {
                            final var problemDefinition = Dem.configValue(GelUiFileSystem.class)
                                    .readString("src/main/resources/html/net/splitcells/gel/ui/editor/code/examples/school-course-scheduling-problem.txt");
                            final String demands = ColloquiumPlanningDemandsTestData.testData();
                            final String supplies = ColloquiumPlanningSuppliesTestData.testData();
                            final var testResult = solutionCalculator().process(request(SolutionCalculator.PATH
                                    , tree("")
                                            .withProperty(PROBLEM_DEFINITION, tree(problemDefinition))
                                            .withProperty(DEMANDS, tree(demands))
                                            .withProperty(SUPPLIES, tree(supplies))));
                            requireEquals(testResult.data().namedChildren(SOLUTION_RATING).get(0).child(0)
                                            .createToJsonPrintable().toJsonString()
                                    , "{\"Cost\":\"0.0\"}");
                            /* TODO Improve solver to solve this example.
                             * requireEquals(testResult.data().namedChildren(SOLUTION_RATING).get(0).child(0)
                             *               .createToJsonPrintable().toJsonString()
                             *       , "{\"Cost\":\"0.0\"}");
                             * */
                        },
                        env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(truthful())))
                .requireErrorFree();
    }
}
