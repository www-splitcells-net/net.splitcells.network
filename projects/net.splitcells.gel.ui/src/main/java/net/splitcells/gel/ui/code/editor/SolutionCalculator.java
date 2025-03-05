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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.solution.SolutionEditor;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.CsvDocument.toCsvString;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.solution.optimization.DefaultOptimization.defaultOptimization;
import static net.splitcells.gel.ui.code.editor.EditorLangParsing.editorLangParsing;
import static net.splitcells.website.server.processor.Response.response;

public class SolutionCalculator implements Processor<Tree, Tree> {
    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/code/editor/calculate-solution.form");
    public static final String PROBLEM_DEFINITION = "net-splitcells-gel-ui-editor-form-problem-definition";

    public static final String SOLUTION_RATING = "net-splitcells-gel-ui-editor-form-solution-rating";
    public static final String SOLUTION = "net-splitcells-gel-ui-editor-form-solution";
    public static final String DEMANDS = "net-splitcells-gel-ui-editor-form-demands";
    public static final String SUPPLIES = "net-splitcells-gel-ui-editor-form-supplies";

    public static final String ERRORS = "net-splitcells-gel-ui-editor-form-errors";
    public static final String FORM_UPDATE = "net-splitcells-websiter-server-form-update";

    public static SolutionCalculator solutionCalculator() {
        return new SolutionCalculator();
    }

    private SolutionCalculator() {

    }

    public Result<SolutionEditor, Tree> parseSolutionCodeEditor(String editorCode) {
        final Result<SolutionEditor, Tree> editorParsing = result();
        final var solutionDescription = editorLangParsing(editorCode);
        if (solutionDescription.defective()) {
            return editorParsing.withErrorMessages(solutionDescription);
        }
        return editor("editor", EXPLICIT_NO_CONTEXT).solutionEditor(solutionDescription.value().orElseThrow());
    }

    @Override
    public Response<Tree> process(Request<Tree> request) {
        final var formUpdate = tree(FORM_UPDATE);
        try {
            PATH.requireEqualityTo(request.trail());
            final var solutionEditorParsing = parseSolutionCodeEditor(request
                    .data()
                    .namedChild(PROBLEM_DEFINITION)
                    .child(0)
                    .name());
            final var isSolutionEditorParsed = solutionEditorParsing.value().isPresent();
            if (isSolutionEditorParsed) {
                final var solutionEditor = solutionEditorParsing.value().orElseThrow();
                final var solution = solutionEditor.solution().orElseThrow();
                final var demandDefinitions = request.data().namedChildren(DEMANDS);
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
                if (solutionEditor.columnAttributesForOutputFormat().hasElements()
                        || solutionEditor.rowAttributesForOutputFormat().hasElements()) {
                    final var reformattedSolution = solution.toReformattedTable
                            (solutionEditor.columnAttributesForOutputFormat().mapped(solution::attributeByName)
                                    , solutionEditor.rowAttributesForOutputFormat().mapped(solution::attributeByName));
                    final List<List<String>> csvContent = list();
                    csvContent.addAll(rangeClosed(1, reformattedSolution.get(0).size())
                            .mapToObj(i -> "" + i)
                            .collect(toList()));
                    csvContent.addAll(reformattedSolution);
                    formUpdate.withProperty(SOLUTION, toCsvString(csvContent));
                } else {
                    formUpdate.withProperty(SOLUTION, solution.toSimplifiedCSV());
                }
                formUpdate.withProperty(SOLUTION_RATING, solution.constraint().rating().toTree());
            }
            if (solutionEditorParsing.errorMessages().hasElements() || !isSolutionEditorParsed) {
                final var errorReport = tree("Errors solving the given problem.");
                if (!isSolutionEditorParsed) {
                    errorReport.withChild(tree("Could not parse problem."));
                }
                errorReport.withChildren(solutionEditorParsing.errorMessages());
                formUpdate.withProperty(ERRORS, errorReport.toCommonMarkString());
            }
        } catch (Throwable t) {
            logs().appendError(t);
            formUpdate.withProperty(ERRORS, tree("The program had an internal error and therefore a solution could not be calculated."));
        }
        if (formUpdate.namedChildren(ERRORS).isEmpty()) {
            // Ensures, that the error field in the front end is cleared, if no errors are present.
            formUpdate.withProperty(ERRORS, tree(""));
        }
        return response(formUpdate);
    }

    private static String standardizeInput(String arg) {
        return arg.replace("\n\r", "\n").replace("\r\n", "\n");
    }
}
