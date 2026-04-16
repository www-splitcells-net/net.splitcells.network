/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.LogMessage;
import net.splitcells.dem.testing.Result;
import net.splitcells.dem.testing.need.Need;
import net.splitcells.dem.testing.reporting.ErrorReporter;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.EditorData;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;
import net.splitcells.website.server.security.access.AccessContainer;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.CommonMarkUtils.joinDocuments;
import static net.splitcells.dem.lang.tree.CommonMarkConfig.commonMarkConfig;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.resource.ContentType.TEXT;
import static net.splitcells.dem.resource.communication.log.LogMessageI.logMessage;
import static net.splitcells.dem.testing.need.NeedsCheck.runWithCheckedNeeds;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.*;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.website.Format.*;
import static net.splitcells.website.server.messages.FormUpdate.*;
import static net.splitcells.website.server.processor.Response.response;
import static net.splitcells.website.server.security.authentication.Authentication.lifeCycleId;

public class EditorProcessor implements Processor<Tree, Tree> {

    public static final String PROBLEM_DEFINITION = "Definition";
    private static final String REQUESTING_ASYNC = "requesting-async";
    private static final String IS_OPTIMIZING = "is-optimizing";
    private static final String ASYNC_ID = "async-user-session-life-cycle-id";
    private static final String REQUEST_UPDATE_FOR_ASYNC_ID = "request-async-update-for-life-cycle-id";

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/editor/geal/form");

    public static EditorProcessor editorProcessor() {
        return new EditorProcessor();
    }

    private final AccessContainer<Editor> editorAccess = configValue(EditorAccess.class);

    private EditorProcessor() {

    }

    private Response<Tree> process(Request<Tree> request, Editor editor) {
        val userSession = request.userSession();
        val problemDefinition = request.data().namedChild(PROBLEM_DEFINITION, needInput(PROBLEM_DEFINITION));
        val inputValues = request.data().children();
        if (inputValues.hasElements()) {
            inputValues.stream()
                    .forEach(c -> {
                        val content = toBytes(c.child(0).name());
                        // TODO Support multiple formats of data.
                        editor.saveData(c.name(), editorData(TEXT_PLAIN, content));
                    });
        }
        editor.interpret(problemDefinition.content());
        val formUpdate = tree(FORM_UPDATE);
        val dataTypes = tree(DATA_TYPES).withParent(formUpdate);
        val dataValues = tree(DATA_VALUES).withParent(formUpdate);
        val renderingTypes = tree(RENDERING_TYPES).withParent(formUpdate);
        val asyncs = request.data().namedChildren(REQUESTING_ASYNC);
        val isSync = asyncs.isEmpty() || !"true".equals(asyncs.get(0).valueName());
        if (isSync) {
            editor.optimize();
        } else {
            editor.setOptimizing(true);
            dataValues.withProperty(ASYNC_ID, tree(lifeCycleId(userSession)));
            dataTypes.withProperty(ASYNC_ID, TEXT.codeName());
            renderingTypes.withProperty(ASYNC_ID, PLAIN_TEXT);
            dataValues.withProperty(IS_OPTIMIZING, tree("" + editor.isOptimizing()));
            dataTypes.withProperty(IS_OPTIMIZING, TEXT.codeName());
            renderingTypes.withProperty(IS_OPTIMIZING, PLAIN_TEXT);
        }
        editor.getTables().entrySet().forEach(e -> {
            dataValues.withProperty(e.getKey(), e.getValue().toCSV(reportInvalidCsvData(e.getKey())));
            dataTypes.withProperty(e.getKey(), CSV.mimeTypes());
            renderingTypes.withProperty(e.getKey(), INTERACTIVE_TABLE);
            if (editor.getTableFormatting().hasKey(e.getKey())) {
                val formatting = editor.getTableFormatting().get(e.getKey());
                val formattingKey = e.getKey() + ".formatted";
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
            val ratingDescriptionKey = e.getKey() + ".rating.report";
            dataValues.withProperty(ratingDescriptionKey, e.getValue().constraint().commonMarkRatingReport());
            dataTypes.withProperty(ratingDescriptionKey, COMMON_MARK.mimeTypes());
            renderingTypes.withProperty(ratingDescriptionKey, PLAIN_TEXT);
            if (editor.getTableFormatting().hasKey(e.getKey())) {
                val formatting = editor.getTableFormatting().get(e.getKey());
                val formattingKey = e.getKey() + ".formatted";
                dataValues.withProperty(formattingKey, e.getValue()
                        .toReformattedCsv(formatting.getColumnAttributes(), formatting.getRowAttributes()));
                dataTypes.withProperty(formattingKey, CSV.mimeTypes());
                renderingTypes.withProperty(formattingKey, INTERACTIVE_TABLE);
            }
        });
        editor.dataKeys().stream().forEach(d -> {
            val data = editor.loadData(d, needDataForOutput(d));
            if (dataValues.namedChildren(d).isEmpty()) {
                dataValues.withProperty(d, parseString(data.getContent(), reportOutputParsing(d)));
                dataTypes.withProperty(d, data.getFormat().mimeTypes());
                renderingTypes.withProperty(d, PLAIN_TEXT);
            }
        });
        editor.getSolutions().entrySet().forEach(solution -> {
            val key = solution.getKey() + ".constraint";
            dataValues.withProperty(key, solution.getValue().constraint()
                    .graph()
                    .toCompactTree()
                    .toCommonMarkString(commonMarkConfig().setUseBlockQuotesForLongNames(false)));
            dataTypes.withProperty(key, COMMON_MARK.mimeTypes());
            renderingTypes.withProperty(key, PLAIN_TEXT);
        });
        if (isSync) {
            editorAccess.delete(userSession);
        } else {
            formUpdate.withProperty(ASYNC_ID, lifeCycleId(userSession));
            Dem.executeThread(EditorProcessor.class, () -> {
                try {
                    editor.optimize();
                } finally {
                    editorAccess.delete(userSession);
                }
            });
        }
        return response(formUpdate);
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
        val userSession = request.userSession();
        val asyncIdUpdate = Optional.of(request.data().namedChildren(REQUEST_UPDATE_FOR_ASYNC_ID))
                .filter(List::hasElements)
                .map(ids -> ids.firstValue().orElseThrow())
                .stream()
                .findFirst();
        Result<Response<Tree>, String> result;
        if (asyncIdUpdate.isPresent()) {
            result = runWithCheckedNeeds(
                    () -> editorAccess.process(asyncEditor -> asyncEditor.processTablesSynchronously(editor -> process(request, editor))
                            , userSession
                            , asyncIdUpdate.orElseThrow().valueName()));
            result.optionalValue().ifPresent(ov -> ov.data().withProperty(ASYNC_ID, asyncIdUpdate.orElseThrow().valueName()));
        } else {
            result = runWithCheckedNeeds(
                    () -> editorAccess.createAndAccess(session -> editor("editor-data-query", EXPLICIT_NO_CONTEXT)
                            , editor -> process(request, editor)
                            , userSession));
        }
        if (result.working()) {
            return result.requiredValue();
        }
        val formUpdate = tree(FORM_UPDATE);
        val dataValues = tree(DATA_VALUES).withParent(formUpdate);
        val dataTypes = tree(DATA_TYPES).withParent(formUpdate);
        dataTypes.withProperty(ERRORS, COMMON_MARK.mimeTypes());
        dataValues.withProperty(ERRORS
                , result.errorMessages()
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
            val namedChildren = arg.namedChildren(name);
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
