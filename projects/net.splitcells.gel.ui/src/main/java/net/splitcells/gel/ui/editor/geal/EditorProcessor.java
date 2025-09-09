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

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.editor.EditorData;
import net.splitcells.website.Format;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.EditorData.editorData;
import static net.splitcells.website.Format.CSV;
import static net.splitcells.website.server.processor.Response.response;

public class EditorProcessor implements Processor<Tree, Tree> {

    public static final String PROBLEM_DEFINITION = "definition";
    public static final String FORM_UPDATE = "net-splitcells-websiter-server-form-update";
    public static final String DATA_VALUES = "data-values";
    public static final String DATA_TYPES = "data-types";
    public static final String RENDERING_TYPES = "rendering-types";
    public static final String INTERACTIVE_TABLE = "interactive-table";
    public static final String PLAIN_TEXT = "plain-text";

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
        final var editor = editor("editor-data-query", EXPLICIT_NO_CONTEXT);
        final var problemDefinition = request.data().namedChildren(PROBLEM_DEFINITION);
        final var inputValues = request.data().children();
        if (inputValues.hasElements()) {
            inputValues.forEach(c -> {
                final var content = toBytes(c.child(0).name());
                // TODO Support multiple formats of data.
                editor.saveData(c.name(), editorData(Format.TEXT_PLAIN, content));
            });
        }
        if (problemDefinition.size() == 1) {
            editor.interpret(problemDefinition.get(0).content());
            final var formUpdate = tree(FORM_UPDATE);
            final var dataTypes = tree(DATA_TYPES).withParent(formUpdate);
            final var dataValues = tree(DATA_VALUES).withParent(formUpdate);
            final var renderingTypes = tree(RENDERING_TYPES).withParent(formUpdate);
            if (editor.getSolutions().size() == 1) {
                editor.getSolutions().values().iterator().next().optimize();
            }
            editor.getTables().entrySet().forEach(e -> {
                dataValues.withProperty(e.getKey(), e.getValue().toCSV());
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
                dataValues.withProperty(e.getKey(), e.getValue().toCSV());
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
            editor.dataKeys().stream().forEach(d -> {
                final var data = editor.loadData(d);
                if (dataValues.namedChildren(d).isEmpty()) {
                    dataValues.withProperty(d, parseString(data.getContent()));
                    dataTypes.withProperty(d, data.getFormat().mimeTypes());
                    renderingTypes.withProperty(d, PLAIN_TEXT);
                }
            });
            return response(formUpdate);
        } else if (problemDefinition.size() > 1) {
            /* TODO An response is always required, as otherwise the client can get its input data lost,
             * because of unstable code.
             */
            throw execException();
        } else {
            throw execException();
        }
    }
}
