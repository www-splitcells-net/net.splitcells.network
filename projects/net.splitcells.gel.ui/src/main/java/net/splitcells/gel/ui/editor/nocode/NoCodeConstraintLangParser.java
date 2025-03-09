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
package net.splitcells.gel.ui.editor.nocode;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.no.code.antlr4.NoCodeDenParser;
import net.splitcells.dem.source.den.DenParser;
import net.splitcells.dem.testing.Result;
import net.splitcells.gel.editor.lang.ConstraintDescription;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Result.result;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.lang.ConstraintDescription.constraintDescription;
import static net.splitcells.gel.editor.solution.SolutionEditor.AFFECTED_CONTENT;

public class NoCodeConstraintLangParser {
    private NoCodeConstraintLangParser() {
    }

    public static Result<ConstraintDescription, Tree> parseConstraintDescription(List<NoCodeDenParser.Function_callContext> functionChain, ParserRuleContext parent) {
        final Result<ConstraintDescription, Tree> constraintDescription = result();
        if (functionChain.isEmpty()) {
            return constraintDescription.withErrorMessage(tree("An empty function chain is invalid.")
                    .withProperty(AFFECTED_CONTENT, parent.getText()));
        }
        return constraintDescription;
    }
}
