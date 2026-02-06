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
package net.splitcells.gel.ui.editor.geal;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.LogMessage;
import net.splitcells.dem.testing.need.Need;
import net.splitcells.dem.testing.reporting.ErrorReporter;
import net.splitcells.gel.editor.EditorData;
import net.splitcells.website.Format;
import net.splitcells.website.server.messages.FormUpdate;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.resource.communication.log.LogMessageI.logMessage;
import static net.splitcells.dem.testing.need.NeedsCheck.runWithCheckedNeeds;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.*;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.website.Format.*;
import static net.splitcells.website.server.messages.FormUpdate.*;
import static net.splitcells.website.server.processor.Response.response;

public class EditorProcessor implements Processor<Tree, Tree> {

    public static final String PROBLEM_DEFINITION = "Definition";

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/editor/geal/form");

    public static EditorProcessor editorProcessor() {
        return new EditorProcessor();
    }

    private EditorProcessor() {

    }

    /**
     * <p>TODO Use a domain object for the results and use serialization for generating the {@link Tree}.</p>
     * <p>TODO Additionally a random tests with probabilistic successes could be supported as well.
     * It should be stored in the network log, how often the test failed or succeeded yet.
     * Another job should check the ratio between failed tests and succeeded ones.</p>
     *
     * @param request
     * @return
     */
    @Override
    public Response<Tree> process(Request<Tree> request) {
        final var endResponse = runWithCheckedNeeds(() -> {
            final var editor = editor("editor-data-query", EXPLICIT_NO_CONTEXT);
            final var problemDefinition = request.data().namedChild(PROBLEM_DEFINITION, needInput(PROBLEM_DEFINITION));
            final var inputValues = request.data().children();
            if (inputValues.hasElements()) {
                inputValues.forEach(c -> {
                    final var content = toBytes(c.child(0).name());
                    // TODO Support multiple formats of data.
                    editor.saveData(c.name(), editorData(TEXT_PLAIN, content));
                });
            }
            editor.interpret(problemDefinition.content());
            final var formUpdate = tree(FORM_UPDATE);
            final var dataTypes = tree(DATA_TYPES).withParent(formUpdate);
            final var dataValues = tree(DATA_VALUES).withParent(formUpdate);
            final var renderingTypes = tree(RENDERING_TYPES).withParent(formUpdate);
            // TODO Optimize any solutions.
            if (editor.getSolutions().size() == 1) {
                editor.getSolutions().values().iterator().next().optimize();
            }
            editor.getTables().entrySet().forEach(e -> {
                dataValues.withProperty(e.getKey(), e.getValue().toCSV(reportInvalidCsvData(e.getKey())));
                dataTypes.withProperty(e.getKey(), CSV.mimeTypes());
                renderingTypes.withProperty(e.getKey(), INTERACTIVE_TABLE);
                if (editor.getTableFormatting().hasKey(e.getKey())) {
                    final var formatting = editor.getTableFormatting().get(e.getKey());
                    final var formattingKey = e.getKey() + ".formatted";
                    dataValues.withProperty(formattingKey, e.getValue()
                            .toReformattedCsv(formatting.getColumnAttributes(), formatting.getRowAttributes()));
                    dataTypes.withProperty(formattingKey, CSV.mimeTypes());
                    renderingTypes.withProperty(formattingKey, INTERACTIVE_TABLE);
                }
            });
            editor.getSolutions().entrySet().forEach(e -> {
                dataValues.withProperty(e.getKey(), e.getValue().toSimplifiedCSV(reportInvalidCsvData(e.getKey())));
                dataTypes.withProperty(e.getKey(), CSV.mimeTypes());
                renderingTypes.withProperty(e.getKey(), INTERACTIVE_TABLE);
                final var ratingDescriptionKey = e.getKey() + ".rating.report";
                dataValues.withProperty(ratingDescriptionKey, e.getValue().constraint().commonMarkRatingReport());
                dataTypes.withProperty(ratingDescriptionKey, COMMON_MARK.mimeTypes());
                renderingTypes.withProperty(ratingDescriptionKey, PLAIN_TEXT);
                if (editor.getTableFormatting().hasKey(e.getKey())) {
                    final var formatting = editor.getTableFormatting().get(e.getKey());
                    final var formattingKey = e.getKey() + ".formatted";
                    dataValues.withProperty(formattingKey, e.getValue()
                            .toReformattedCsv(formatting.getColumnAttributes(), formatting.getRowAttributes()));
                    dataTypes.withProperty(formattingKey, CSV.mimeTypes());
                    renderingTypes.withProperty(formattingKey, INTERACTIVE_TABLE);
                }
            });
            editor.dataKeys().stream().forEach(d -> {
                final var data = editor.loadData(d, needDataForOutput(d));
                if (dataValues.namedChildren(d).isEmpty()) {
                    dataValues.withProperty(d, parseString(data.getContent(), reportOutputParsing(d)));
                    dataTypes.withProperty(d, data.getFormat().mimeTypes());
                    renderingTypes.withProperty(d, PLAIN_TEXT);
                }
            });
            editor.getSolutions().entrySet().forEach(solution -> {
                val key = solution.getKey() + ".constraint";
                dataValues.withProperty(key, solution.getValue().constraint().graph().toCompactTree().toCommonMarkString());
                dataTypes.withProperty(key, COMMON_MARK.mimeTypes());
                renderingTypes.withProperty(key, PLAIN_TEXT);
            });
            return response(formUpdate);
        });
        if (endResponse.working()) {
            return endResponse.requiredValue();
        }
        final var formUpdate = tree(FORM_UPDATE);
        final var dataValues = tree(DATA_VALUES).withParent(formUpdate);
        final var dataTypes = tree(DATA_TYPES).withParent(formUpdate);
        dataTypes.withProperty(ERRORS, COMMON_MARK.mimeTypes());
        dataValues.withProperty(ERRORS
                , endResponse.errorMessages()
                        .stream()
                        .reduce((a, b) -> joinDocuments(a, b))
                        .orElse("")
                , reportReportFailure());
        return response(formUpdate);
    }

    public static ErrorReporter reportReportFailure() {
        return t -> execException(tree(
                        "The solution calculation caused an error, that could not be reported. Reporting the cause of the underlying error is therefore not possible.")
                , t);
    }


    public static ErrorReporter reportOutputParsing(String name) {
        return t -> execException(tree("Could not parse output data as UTF-8.")
                        .withProperty("name", name)
                , t);
    }

    public static ErrorReporter reportInvalidCsvData(String name) {
        return t -> execException(tree("Could not render output table to CSV.")
                        .withProperty("name", name)
                , t);
    }

    public static Need<Map<String, EditorData>> needDataForOutput(String name) {
        return editorData -> {
            List<LogMessage<Tree>> log = list();
            if (editorData.hasNotKey(name)) {
                log.add(logMessage(tree("While loading the data for the output, one data field is missing.")
                                .withProperty("Data name", name)
                        , LogLevel.ERROR));
            }
            return log;
        };
    }

    public static Need<Tree> needInput(String name) {
        return arg -> {
            List<LogMessage<Tree>> log = list();
            final var namedChildren = arg.namedChildren(name);
            if (namedChildren.isEmpty()) {
                log.add(logMessage(tree("The submitted form does not have the field `" + name + "` even though one is required.")
                                .withProperty("form", arg)
                        , LogLevel.ERROR));
            } else if (namedChildren.size() > 1) {
                log.add(logMessage(tree("The submitted form have the field `" + name + "` multiple times, even though only one is allowed.")
                                .withProperty("form", arg)
                        , LogLevel.ERROR));
            }
            return log;
        };
    }
}
