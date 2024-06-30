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
package net.splitcells.gel.ui.no.code.editor;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.CsvDocument.toCsvString;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.gel.solution.optimization.DefaultOptimization.defaultOptimization;
import static net.splitcells.gel.ui.no.code.editor.NoCodeProblemParser.parseNoCodeProblem;
import static net.splitcells.website.server.processor.Response.response;

public class NoCodeSolutionCalculator implements Processor<Perspective, Perspective> {

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/no/code/editor/calculate-solution.form");
    public static final String PROBLEM_DEFINITION = "net-splitcells-gel-ui-no-code-editor-form-problem-definition";

    public static final String SOLUTION_RATING = "net-splitcells-gel-ui-no-code-editor-form-solution-rating";
    public static final String SOLUTION = "net-splitcells-gel-ui-no-code-editor-form-solution";
    public static final String DEMANDS = "net-splitcells-gel-ui-no-code-editor-form-demands";
    public static final String SUPPLIES = "net-splitcells-gel-ui-no-code-editor-form-supplies";

    public static final String ERRORS = "net-splitcells-gel-ui-no-code-editor-form-errors";
    public static final String FORM_UPDATE = "net-splitcells-websiter-server-form-update";

    public static Processor<Perspective, Perspective> noCodeSolutionCalculator() {
        return new NoCodeSolutionCalculator();
    }

    private NoCodeSolutionCalculator() {

    }

    @Override
    public Response<Perspective> process(Request<Perspective> request) {
        final var formUpdate = perspective(FORM_UPDATE);
        try {
            PATH.requireEqualityTo(request.trail());
            final var problemParsing = parseNoCodeProblem(request
                    .data()
                    .namedChild(PROBLEM_DEFINITION)
                    .child(0)
                    .name());
            final var isProblemParsed = problemParsing.value().isPresent();
            if (isProblemParsed) {
                final var problemParameters = problemParsing.value().orElseThrow();
                final var solution = problemParameters
                        .problem()
                        .asSolution();
                final var demandDefinitions = request
                        .data()
                        .namedChildren(DEMANDS);
                if (demandDefinitions.hasElements()) {
                    final var demandsCsv = demandDefinitions.get(0).child(0).name();
                    final var firstLineEnding = demandsCsv.indexOf('\n');
                    final var firstLine = demandsCsv.substring(0, firstLineEnding);
                    for (var attribute : firstLine.split(",")) {
                        solution.demands().attributeByName(attribute.replace("\n", "")
                                .replace("\r", ""));
                    }
                    solution.demands().withAddSimplifiedCsv(
                            standardizeInput(demandsCsv.substring(firstLineEnding + 1)));
                }
                final var supplyDefinitions = request
                        .data()
                        .namedChildren(SUPPLIES);
                if (supplyDefinitions.hasElements()) {
                    final var suppliesCsv = supplyDefinitions.get(0).child(0).name();
                    final var firstLineEnding = suppliesCsv.indexOf('\n');
                    final var firstLine = suppliesCsv.substring(0, firstLineEnding);
                    for (var attribute : firstLine.split(",")) {
                        solution.supplies().attributeByName(attribute.replace("\n", "")
                                .replace("\r", ""));
                    }
                    solution.supplies().withAddSimplifiedCsv(
                            standardizeInput(suppliesCsv.substring(firstLineEnding + 1)));
                }
                defaultOptimization().optimize(solution);
                if (problemParameters.columnAttributesForOutputFormat().hasElements()
                        || problemParameters.rowAttributesForOutputFormat().hasElements()) {
                    final var reformattedSolution = solution.toReformattedTable
                            (problemParameters.columnAttributesForOutputFormat().mapped(solution::attributeByName)
                                    , problemParameters.rowAttributesForOutputFormat().mapped(solution::attributeByName));
                    final List<List<String>> csvContent = list();
                    csvContent.addAll(rangeClosed(1, reformattedSolution.get(0).size())
                            .mapToObj(i -> "" + i)
                            .collect(toList()));
                    csvContent.addAll(reformattedSolution);
                    formUpdate.withProperty(SOLUTION, toCsvString(csvContent));
                } else {
                    formUpdate.withProperty(SOLUTION, solution.toSimplifiedCSV());
                }
                formUpdate.withProperty(SOLUTION_RATING, solution.constraint().rating().toPerspective());
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
        if (formUpdate.namedChildren(ERRORS).isEmpty()) {
            // Ensures, that the error field in the front end is cleared, if no errors are present.
            formUpdate.withProperty(ERRORS, perspective(""));
        }
        return response(formUpdate);
    }

    private static String standardizeInput(String arg) {
        return arg.replace("\n\r", "\n").replace("\r\n", "\n");
    }
}
