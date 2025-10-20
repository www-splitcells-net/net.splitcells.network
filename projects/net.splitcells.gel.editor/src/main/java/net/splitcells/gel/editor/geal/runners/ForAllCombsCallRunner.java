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
package net.splitcells.gel.editor.geal.runners;

import lombok.val;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.editor.EditorParser.ATTRIBUTE_FUNCTION;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class ForAllCombsCallRunner implements FunctionCallRunner {
    public static ForAllCombsCallRunner forAllCombsCallRunner() {
        return new ForAllCombsCallRunner();
    }

    private ForAllCombsCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(FOR_ALL_COMBINATIONS_OF);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        try (val fcr = context.functionCallRecord(ATTRIBUTE_FUNCTION, 1)) {
            final Query subjectVal = fcr.parseQuerySubject(functionCall, subject);
            if (functionCall.getArguments().size() < 2) {
                throw execException(tree("The "
                        + FOR_ALL_COMBINATIONS_OF
                        + " function requires more than one argument, but " + functionCall.getArguments().size() + " were given instead.")
                        .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
            }
            functionCall.getArguments().forEach(a -> {
                if (!(a.getExpression() instanceof FunctionCallDesc)) {
                    throw execException(tree("The "
                            + FOR_ALL_COMBINATIONS_OF
                            + " function only supports function calls as arguments, but " + a.getExpression().getClass().getName()
                            + " was given instead.")
                            .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
                }
            });
            final var groupingAttributes = functionCall.getArguments().stream()
                    .map(a -> {
                        switch (context.parse(a)) {
                            case Attribute<? extends Object> attribute -> {
                                return attribute;
                            }
                            default -> throw execException(tree("Only function calls are supported as argument for "
                                    + FOR_ALL_COMBINATIONS_OF
                                    + ", but "
                                    + a.getExpression().getClass().getName()
                                    + " else was given.")
                                    .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree())
                                    .withProperty("Affected argument", a.getSourceCodeQuote().userReferenceTree()));
                        }

                    })
                    .toList();
            run.setResult(Optional.of(subjectVal.forAllCombinationsOf(groupingAttributes)));
            return run;
        }
    }
}
