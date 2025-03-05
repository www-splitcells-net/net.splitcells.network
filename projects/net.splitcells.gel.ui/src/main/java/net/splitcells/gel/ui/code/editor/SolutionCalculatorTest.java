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

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.ui.code.editor.SolutionCalculator.PROBLEM_DEFINITION;
import static net.splitcells.gel.ui.code.editor.SolutionCalculator.solutionCalculator;
import static net.splitcells.website.server.processor.Request.request;

public class SolutionCalculatorTest {
    @UnitTest
    public void testMinimal() {
        final var testData = "demands={a=integer();b=string()};\n"
                + "supplies={c=integer()};\n"
                + "constraints=forEach(a).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
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
}
