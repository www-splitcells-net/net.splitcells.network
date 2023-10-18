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
package net.splitcells.gel.problem;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.Then;
import org.antlr.v4.runtime.*;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requirePresenceOf;
import static net.splitcells.gel.problem.ProblemParser.parseProblem;

public class ProblemParserTest {
    @UnitTest
    public void test() {
        final var testData = "demands=forAll().then();";
        final var lexer = new net.splitcells.dem.lang.perspective.antlr4.DenLexer(CharStreams.fromString(testData));
        final var parser = new net.splitcells.dem.lang.perspective.antlr4.DenParser(new CommonTokenStream(lexer));
        final var testResult = parser.source_unit();
        requireEquals(testResult.statement().get(0).variable_definition().Name().getText()
                , "demands");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().Name().getText()
                , "forAll");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().access().Name().getText()
                , "then");
    }

    @UnitTest
    public void testParseProblem() {
        final var testData = "demands={a=int();b=string()};"
                + "supplies={c=float()};"
                + "constraints=forEach(a).then(hasSize(2));"
                + "constraints().forEach(b).then(allSame(c));"
                + "name=\"testParseProblem\";";
        final var testSubject = parseProblem(testData);
        final var forEachA = testSubject.constraint().child(0);
        requireEquals(forEachA.type(), ForAll.class);
        requirePresenceOf(forEachA.arguments().get(0).toPerspective().pathOfValueTree(
                "rater-based-on-grouping"
                , "grouping"
                , "for-all-attribute-values"
                , "attribute"
                , "Attribute"
                , "name"
                , "a"));
        final var thenHasSize2 = forEachA.child(0);
        requireEquals(thenHasSize2.type(), Then.class);
        requirePresenceOf(thenHasSize2.arguments().get(0).toPerspective().pathOfValueTree(
                "has-size"
                , "target-size"
                , "2"));
        final var forEachB = testSubject.constraint().child(1);
        requireEquals(forEachB.type(), ForAll.class);
        requirePresenceOf(forEachB.arguments().get(0).toPerspective().pathOfValueTree(
                "rater-based-on-grouping"
                , "grouping"
                , "for-all-attribute-values"
                , "attribute"
                , "Attribute"
                , "name"
                , "b"));
        final var thenAllSameC = forEachB.child(0);
        requireEquals(thenAllSameC.type(), Then.class);
        requirePresenceOf(thenAllSameC.arguments().get(0).toPerspective().pathOfValueTree(
                "all-same"
                , "attribute"
                , "Attribute"
                , "name"
                , "c"));
    }

    @UnitTest
    public void testInvalidDemandAttribute() {
        final var testData = "demands={a=invalid_attribute()};"
                + "supplies={};"
                + "constraints=forEach(a);"
                + "name=\"testInvalidDemandAttribute\";";
        assertThrows(Throwable.class, () -> parseProblem(testData));
    }

    @UnitTest
    public void testInvalidSupplyAttribute() {
        final var testData = "demands={};"
                + "supplies={a=invalid_attribute()};"
                + "constraints=forEach(a);"
                + "name=\"testInvalidDemandAttribute\";";
        assertThrows(Throwable.class, () -> parseProblem(testData));
    }
}
