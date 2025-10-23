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
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.editor.EditorParser.SOLUTION_FUNCTION;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class SolutionCallRunner implements FunctionCallRunner {
    public static SolutionCallRunner solutionCallRunner() {
        return new SolutionCallRunner();
    }

    private static class Args {
        String solutionName;
        Table demands;
        Table supplies;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(fcr -> {
        val args = new Args();
        fcr.requireArgumentCount(3);
        args.solutionName = fcr.parseArgument(String.class, 0);
        args.demands = fcr.parseArgument(Table.class, 1);
        args.supplies = fcr.parseArgument(Table.class, 2);
        return args;
    }, SOLUTION_FUNCTION, 1);

    private SolutionCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(SOLUTION_FUNCTION);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (functionCall.getArguments().isEmpty()) {
            return run.setResult(Optional.of(context.<Solution>resolve(functionCall.getName(), functionCall)));
        }
        if (!supports(functionCall)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        final Optional<Object> result = Optional.of(defineProblem(args.solutionName)
                .withDemands(args.demands)
                .withSupplies(args.supplies)
                .withConstraint(forAll())
                .toProblem()
                .asSolution());
        return functionCallRun(subject, context).setResult(result);
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
