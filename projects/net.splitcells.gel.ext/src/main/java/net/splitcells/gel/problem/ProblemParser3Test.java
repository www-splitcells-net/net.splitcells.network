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

import net.splitcells.dem.testing.annotations.UnitTest;
import org.antlr.v4.runtime.*;

import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.problem.ProblemParser3.parseProblem;

public class ProblemParser3Test {
    @UnitTest
    public void test() {
        final var testData = "demands=forAll().then();";
        final var lexer = new net.splitcells.gel.ext.problem.antlr4.ProblemLexer(CharStreams.fromString(testData));
        final var parser = new net.splitcells.gel.ext.problem.antlr4.ProblemParser(new CommonTokenStream(lexer));
        final var testResult = parser.source_unit();
        requireEquals(testResult.statement().get(0).variable_definition().Name().getText()
                , "demands");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().Name().getText()
                , "forAll");
        requireEquals(testResult.statement().get(0).variable_definition().function_call().access().get(0).Name().getText()
                , "then");
    }

    @UnitTest
    public void testParseProblem() {
        final var testData = "demands={a=int();b=string()};"
                + "supplies={c=float()};"
                + "constraints=forAll();"
                //+ "constraints.forEach(a).then(hasSize(2));"
                //+ "constraints.forEach(b).then(allSame(c));"
                + "name=\"testParseProblem\";";
        ProblemParser2.parseProblem(testData);
        parseProblem(testData);
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
