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

import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class ThenCallRunner implements FunctionCallRunner {
    public static ThenCallRunner thenCallRunner() {
        return new ThenCallRunner();
    }

    private ThenCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(THEN_NAME);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        if (subject.isEmpty()) {
            throw execException(tree("The "
                    + THEN_NAME
                    + " function requires a Solution or Query as a subject, but no was given.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        if (!(subject.orElseThrow() instanceof Solution) && !(subject.orElseThrow() instanceof Query)) {
            throw execException(tree("The "
                    + THEN_NAME
                    + " function requires a Solution or Query as a subject, but "
                    + subject.orElseThrow().getClass().getName()
                    + " was given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        if (functionCall.getArguments().size() != 1) {
            throw execException(tree("The "
                    + THEN_NAME
                    + " function requires exactly one argument, but " + functionCall.getArguments().size() + " were given instead.")
                    .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        final Query subjectVal;
        switch (subject.orElseThrow()) {
            case Solution s -> subjectVal = query(s.constraint());
            case Query q -> subjectVal = q;
            default -> throw execException("Subject has to be a solution or a query: " + subject.orElseThrow());
        }
        final var rawRater = context.parse(functionCall.getArguments().get(0));
        switch (rawRater) {
            case Rater r -> run.setResult(Optional.of(subjectVal.then(r)));
            default ->
                    throw execException(tree("The argument of the then constraint requires exactly one argument, that has to be a rater. Instead a " + rawRater.getClass().getName() + " was given instead.")
                            .withChild(functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        return run;
    }
}
