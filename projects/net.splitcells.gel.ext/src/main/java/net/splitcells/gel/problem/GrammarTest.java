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

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import static net.splitcells.dem.testing.Assertions.requireEquals;

public class GrammarTest {
    @UnitTest
    public void testDemandDefinition() {
        final var testData = "demands=forAll();";
        final var lexer = new net.splitcells.gel.ext.problem.ProblemLexer(CharStreams.fromString(testData));
        final var parser = new net.splitcells.gel.ext.problem.ProblemParser(new CommonTokenStream(lexer));
        final var testResult = parser.source_unit();
        requireEquals(testResult.demands_definition().function_call().Name().getText()
                , "forAll");
    }

    @UnitTest
    public void testSupplyDefinition() {
        final var testData = "supplies=forAll();";
        final var lexer = new net.splitcells.gel.ext.problem.ProblemLexer(CharStreams.fromString(testData));
        final var parser = new net.splitcells.gel.ext.problem.ProblemParser(new CommonTokenStream(lexer));
        final var testResult = parser.source_unit();
        requireEquals(testResult.supplies_definition().function_call().Name().getText()
                , "forAll");
    }
}
