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

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;
import static net.splitcells.gel.ui.ProblemParser.parseProblem;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.processor.Response.response;

public class SolutionCalculator implements Processor<Perspective, Perspective> {

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/calculate-solution.form");
    public static final String PROBLEM_DEFINITION = "net-splitcells-gel-ui-editor-form-problem-definition";
    public static final String SOLUTION = "net-splitcells-gel-ui-editor-form-solution";
    public static final String DEMANDS = "net-splitcells-gel-ui-editor-form-demands";
    public static final String SUPPLIES = "net-splitcells-gel-ui-editor-form-supplies";

    public static final String ERRORS = "net-splitcells-gel-ui-editor-form-errors";
    public static final String FORM_UPDATE = "net-splitcells-websiter-server-form-update";

    public static Processor<Perspective, Perspective> solutionCalculator() {
        return new SolutionCalculator();
    }

    private SolutionCalculator() {

    }

    @Override
    public Response<Perspective> process(Request<Perspective> request) {
        final var formUpdate = perspective(FORM_UPDATE);
        try {
            PATH.requireEqualityTo(request.trail());
            final var problemParsing = parseProblem(request
                    .data()
                    .namedChild(PROBLEM_DEFINITION)
                    .child(0)
                    .name());
            final var isProblemParsed = problemParsing.value().isPresent();
            if (isProblemParsed) {
                final var solution = problemParsing.value().orElseThrow()
                        .asSolution();
                final var demandDefinitions = request
                        .data()
                        .namedChildren(DEMANDS);
                if (demandDefinitions.hasElements()) {
                    solution.demandsFree().withAddSimplifiedCsv(
                            standardizeInput(demandDefinitions.get(0).child(0).name()));
                }
                final var supplyDefinitions = request
                        .data()
                        .namedChildren(SUPPLIES);
                if (supplyDefinitions.hasElements()) {
                    solution.suppliesFree().withAddSimplifiedCsv(
                            standardizeInput(supplyDefinitions.get(0).child(0).name()));
                }
                constraintGroupBasedRepair(repairConfig()).optimize(solution);
                onlineLinearInitialization().optimize(solution);
                formUpdate.withProperty(SOLUTION, solution.toSimplifiedCSV());
            }
            if (problemParsing.errorMessages().hasElements() || !isProblemParsed) {
                final var errorReport = perspective("Errors solving the given problem.");
                if (!isProblemParsed) {
                    errorReport.withChild(perspective("Could not parse problem."));
                }
                errorReport.withChildren(problemParsing.errorMessages());
                formUpdate.withProperty(ERRORS, errorReport.toCommonMarkString());
            }
        } catch (Throwable t) {
            logs().appendError(t);
            formUpdate.withProperty(ERRORS, perspective("The program had an internal error and therefore a solution could not be calculated."));
        }
        return response(formUpdate);
    }

    private static String standardizeInput(String arg) {
        return arg.replace("\n\r", "\n").replace("\r\n", "\n");
    }
}
