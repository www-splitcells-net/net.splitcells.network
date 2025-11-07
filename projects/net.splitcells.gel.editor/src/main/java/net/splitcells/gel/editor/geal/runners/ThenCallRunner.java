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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.FOR_ALL_COMBINATIONS_OF;
import static net.splitcells.gel.constraint.type.Then.THEN_NAME;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;

public class ThenCallRunner implements FunctionCallRunner {
    public static ThenCallRunner thenCallRunner() {
        return new ThenCallRunner();
    }

    private static class Args {
        Query subjectVal;
        Rater rater;
    }

    private static final String DESCRIPTION = """
            Creates and returns a constraint node, that groups and rates incoming lines by the given rater.
            Lines with a none zero cost are not propagated to the child constraints.
            The constraint node is added to the receiver, which is the parent constraint.
            The rating of a given line in a then constraint node is the sum of the constraint node's rating and its children's rating.
            """;

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(THEN_NAME, 1
            , fcr -> {
                val args = new Args();
                args.subjectVal = fcr.parseQuerySubject();
                fcr.requireArgumentCount(1);
                args.rater = fcr.parseArgument(Rater.class, 0, "rater");
                fcr.addDescription(tree("paragraph", SEW).withText(DESCRIPTION));
                return args;
            });

    private ThenCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        return functionCall.getName().getValue().equals(THEN_NAME);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        val run = functionCallRun(subject, context);
        if (!supports(functionCall, subject, context)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        return run.setResult(Optional.of(args.subjectVal.then(args.rater)));
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
