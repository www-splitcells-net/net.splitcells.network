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
import net.splitcells.website.Format;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.Request;
import net.splitcells.website.server.processor.Response;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.website.Format.CSV;
import static net.splitcells.website.server.processor.Response.response;

public class EditorProcessor implements Processor<Tree, Tree> {

    public static final String PROBLEM_DEFINITION = "net-splitcells-gel-ui-editor-geal-form-problem-definition";
    public static final String FORM_UPDATE = "net-splitcells-websiter-server-form-update";
    public static final String DATA_MAP = "data-map";
    public static final String DATA_VALUES = "data-values";

    public static final Trail PATH = Trail.trail("net/splitcells/gel/ui/editor/geal/form");

    public static EditorProcessor editorProcessor() {
        return new EditorProcessor();
    }

    private EditorProcessor() {

    }

    @Override
    public Response<Tree> process(Request<Tree> request) {
        final var problemDefinition = request.data().namedChildren(PROBLEM_DEFINITION);
        if (problemDefinition.size() == 1) {
            final var editor = editor("editor-data-query", EXPLICIT_NO_CONTEXT);
            editor.interpret(problemDefinition.get(0).content());
            final var formUpdate = tree(FORM_UPDATE);
            final var dataMap = tree(DATA_MAP);
            formUpdate.withChild(dataMap);
            final var dataValues = tree(DATA_VALUES);
            formUpdate.withChild(dataValues);
            if (editor.getSolutions().size() == 1) {
                editor.getSolutions().values().iterator().next().optimize();
            }
            editor.dataKeys().stream().forEach(d -> {
                final var data = editor.loadData(d);
                dataValues.withProperty(d, parseString(data.getContent()));
                dataMap.withProperty(d, data.getFormat().mimeTypes());
            });
            editor.getTables().entrySet().forEach(e -> {
                dataValues.withProperty(e.getKey(), e.getValue().toCSV());
                dataMap.withProperty(e.getKey(), CSV.mimeTypes());
            });
            editor.getSolutions().entrySet().forEach(e -> {
                dataValues.withProperty(e.getKey(), e.getValue().toCSV());
                dataMap.withProperty(e.getKey(), CSV.mimeTypes());
            });
            return response(formUpdate);
        } else if (problemDefinition.size() > 1) {
            throw execException();
        } else {
            throw execException();
        }
    }
}
