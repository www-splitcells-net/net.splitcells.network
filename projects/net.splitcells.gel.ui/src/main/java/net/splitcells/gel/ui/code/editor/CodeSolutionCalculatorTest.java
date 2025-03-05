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

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.data.view.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.stringAttribute;
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
                + "constraints=forEach(a).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
        final var testResult = solutionCalculator().parseSolutionCodeEditor(testData);
        testResult.requireWorking();
        final var solution = testResult.value().orElseThrow().solution().orElseThrow();
        solution.headerView2().requireContentsOf((a, b) -> a.equalContentTo(b)
                , integerAttribute("a")
                , stringAttribute("b")
                , integerAttribute("c"));
    }
}
