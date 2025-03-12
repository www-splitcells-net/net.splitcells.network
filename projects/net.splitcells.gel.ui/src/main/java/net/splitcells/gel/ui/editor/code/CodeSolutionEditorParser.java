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
package net.splitcells.gel.ui.editor.code;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.solution.SolutionEditor;
import net.splitcells.website.server.processor.Request;

import java.util.function.Function;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.ui.editor.code.CodeEditorLangParser.codeEditorLangParsing;

public class CodeSolutionEditorParser implements Function<Request<Tree>, Result<SolutionEditor, Tree>> {
    public static final String PROBLEM_DEFINITION = "net-splitcells-gel-ui-editor-code-form-problem-definition";
    public static CodeSolutionEditorParser codeSolutionEditorParser() {
        return new CodeSolutionEditorParser();
    }
    private CodeSolutionEditorParser() {

    }

    @Override
    public Result<SolutionEditor, Tree> apply(Request<Tree> request) {
        final Result<SolutionEditor, Tree> editorParsing = result();
        final var problemDefinition = request
                .data()
                .namedChildren(PROBLEM_DEFINITION);
        final var problemDefinitionSize = problemDefinition.size();
        if (problemDefinitionSize == 0) {
            return editorParsing;
        } else if (problemDefinitionSize > 1) {
            return editorParsing.withErrorMessage(tree("More than 1 problem definition was given."));
        }
        final var editorCode = problemDefinition
                .get(0)
                .child(0)
                .name();
        final var solutionDescription = codeEditorLangParsing(editorCode);
        if (solutionDescription.defective()) {
            return editorParsing.withErrorMessages(solutionDescription);
        }
        return editor("editor", EXPLICIT_NO_CONTEXT).solutionEditor(solutionDescription.optionalValue().orElseThrow());
    }
}
