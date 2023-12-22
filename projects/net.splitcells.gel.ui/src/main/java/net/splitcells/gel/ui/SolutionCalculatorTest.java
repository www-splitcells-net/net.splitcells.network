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
package net.splitcells.gel.ui;

import net.splitcells.dem.Dem;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.CapabilityTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.constraint.type.ForAll;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.projects.extension.ColloquiumPlanningDemandsTestData;
import net.splitcells.website.server.projects.extension.ColloquiumPlanningSuppliesTestData;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requirePresenceOf;
import static net.splitcells.gel.ui.ProblemParser.parseProblem;
import static net.splitcells.gel.ui.SolutionCalculator.DEMANDS;
import static net.splitcells.gel.ui.SolutionCalculator.PROBLEM_DEFINITION;
import static net.splitcells.gel.ui.SolutionCalculator.SOLUTION_RATING;
import static net.splitcells.gel.ui.SolutionCalculator.SUPPLIES;
import static net.splitcells.gel.ui.SolutionCalculator.solutionCalculator;
import static net.splitcells.website.server.processor.Request.request;
import static net.splitcells.website.server.projects.extension.ColloquiumPlanningDemandsTestData.colloquiumPlanningDemandTestData;

public class SolutionCalculatorTest {
    @UnitTest
    public void testMinimal() {
        final var testData = "demands={a=int();b=string()};\n"
                + "supplies={c=float()};\n"
                + "constraints=forEach(a).then(hasSize(2));\n"
                + "constraints().forEach(b).then(allSame(c));\n"
                + "name=\"testParseProblem\";\n";
        final var testResult = solutionCalculator().process(request(SolutionCalculator.PATH
                , perspective("").withProperty(PROBLEM_DEFINITION,
                        perspective(testData))));
        requireEquals(testResult.data()
                        .propertyInstance(SolutionCalculator.SOLUTION)
                        .orElseThrow()
                        .child(0)
                        .name()
                , "a,b,c,argumentation\n");
    }

    @UnitTest
    public void testDemonstrationExample() {
        final var problemDefinition = Dem.configValue(GelUiFileSystem.class)
                .readString("src/main/resources/html/net/splitcells/gel/ui/examples/school-course-scheduling-problem.txt");
        final String demands = ColloquiumPlanningDemandsTestData.testData();
        final String supplies = ColloquiumPlanningSuppliesTestData.testData();
        final var testResult = solutionCalculator().process(request(SolutionCalculator.PATH
                , perspective("")
                        .withProperty(PROBLEM_DEFINITION, perspective(problemDefinition))
                        .withProperty(DEMANDS, perspective(demands))
                        .withProperty(SUPPLIES, perspective(supplies))));
        requireEquals(testResult.data().namedChildren(SOLUTION_RATING).get(0).child(0)
                        .createToJsonPrintable().toJsonString()
                , "{\"Cost\":\"0.0\"}");
    }
}
