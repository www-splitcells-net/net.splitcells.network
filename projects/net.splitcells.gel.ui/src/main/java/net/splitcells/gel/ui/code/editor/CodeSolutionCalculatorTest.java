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
package net.splitcells.gel.ui.code.editor;

import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.editor.lang.AttributeDescription;
import net.splitcells.gel.ui.GelUiFileSystem;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requirePresenceOf;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
import static net.splitcells.gel.editor.lang.ReferenceDescription.referenceDescription;
import static net.splitcells.gel.rating.rater.lib.HasSize.hasSize;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllValueCombinations.FOR_ALL_VALUE_COMBINATIONS_NAME;
import static net.splitcells.gel.ui.ProblemParser.parseProblem;
import static net.splitcells.gel.ui.code.editor.CodeEditorLangParser.editorLangParsing;
import static net.splitcells.gel.ui.code.editor.CodeSolutionCalculator.PROBLEM_DEFINITION;
import static net.splitcells.gel.ui.code.editor.CodeSolutionCalculator.solutionCalculator;
import static net.splitcells.website.server.processor.Request.request;

public class CodeSolutionCalculatorTest {
    @UnitTest
    public void testMinimalProcess() {
        final var testData = "demands={a=integer();b=string()};\n"
                + "supplies={c=integer()};\n"
                + "constraints=forEach(a).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
        final var testResult = solutionCalculator().process(request(CodeSolutionCalculator.PATH
                , tree("").withProperty(PROBLEM_DEFINITION,
                        tree(testData))));
        requireEquals(testResult.data()
                        .propertyInstance(CodeSolutionCalculator.SOLUTION)
                        .orElseThrow()
                        .child(0)
                        .name()
                , "a,b,c,argumentation\n");
    }

    @UnitTest
    public void testMinimalProblem() {
        final var testData = "demands={a=integer();b=string()};\n"
                + "supplies={c=integer()};\n"
                + "constraints=forEach(a).forEach(b).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
        final var testResult = solutionCalculator().parseSolutionCodeEditor(testData);
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
        final var resultData = editorLangParsing(Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/examples/school-course-scheduling-problem.txt")).requireWorking()
                .optionalValue().orElseThrow();
        resultData.columnAttributesForOutputFormat().requireEqualityTo(list(referenceDescription("roomNumber", AttributeDescription.class)));
        resultData.rowAttributesForOutputFormat().requireEqualityTo(list(referenceDescription("date", AttributeDescription.class)
                , referenceDescription("shift", AttributeDescription.class)));
    }

    @UnitTest
    public void testParseProblem() {
        final var testData = "demands={a=int();b=string()};\n"
                + "supplies={c=integer()};\n"
                + "constraints=forEach(a).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
        final var testSubject = solutionCalculator().parseSolutionCodeEditor(testData)
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
        final var testData = "demands = {a = int(); b = string()};\n"
                + "supplies = {c = float()};\n"
                + "constraints = forAllCombinationsOf(a, b, c);\n"
                + "name = \"testParseProblem\";\n";
        final var testSubject = parseProblem(testData).optionalValue().orElseThrow().problem();
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
        final var testData = "demands={a=invalid_attribute()};"
                + "supplies={};"
                + "constraints=forEach(a);"
                + "name=\"testInvalidDemandAttribute\";";
        parseProblem(testData).errorMessages().requireAnyContent();
    }

    @UnitTest
    public void testInvalidSupplyAttribute() {
        final var testData = "demands={};"
                + "supplies={a=invalid_attribute()};"
                + "constraints=forEach(a);"
                + "name=\"testInvalidDemandAttribute\";";
        parseProblem(testData).errorMessages().requireAnyContent();
    }
}
