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

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;
import net.splitcells.gel.solution.Solution;

import java.util.Optional;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.editor.EditorParser.SOLUTION_FUNCTION;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

public class SolutionCallRunner implements FunctionCallRunner {
    public static SolutionCallRunner solutionCallRunner() {
        return new SolutionCallRunner();
    }

    private SolutionCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(SOLUTION_FUNCTION);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (functionCall.getArguments().isEmpty()) {
            return run.setResult(Optional.of(context.<Solution>resolve(functionCall.getName())));
        }
        if (!supports(functionCall)) {
            return run;
        }
        if (functionCall.getArguments().size() != 3) {
            throw execException(tree("The solution function requires exactly 4 arguments, but " + functionCall.getArguments().size() + " were given.")
                    .withProperty("Affected function call", functionCall.getSourceCodeQuote().userReferenceTree()));
        }
        final var first = functionCall.getArguments().get(0).getExpression();
        final String solutionName;
        switch (first) {
            case StringDesc n -> solutionName = n.getValue();
            default ->
                    throw execException("The 1st argument has to be the solution name represented by a string, but is a " + first.getClass() + " was given.");
        }
        final var second = functionCall.getArguments().get(1).getExpression();
        final Table demands;
        switch (second) {
            case FunctionCallDesc n -> {
                if (n.getArguments().hasElements()) {
                    throw notImplementedYet();
                }
                demands = context.getTables().get(n.getName().getValue());
            }
            default ->
                    throw execException("The 2nd argument has to be the demand table represented by a variable name, but a " + second.getClass() + " was given instead.");
        }
        final var third = functionCall.getArguments().get(2).getExpression();
        final Table supplies;
        switch (third) {
            case FunctionCallDesc n -> {
                if (n.getArguments().hasElements()) {
                    throw notImplementedYet();
                }
                supplies = context.getTables().get(n.getName().getValue());
            }
            default ->
                    throw execException("The 3rd argument has to be the supply table represented by a variable name, but a " + second.getClass() + " was given instead.");
        }
        final Optional<Object> result = Optional.of(defineProblem(solutionName)
                .withDemands(demands)
                .withSupplies(supplies)
                .withConstraint(forAll())
                .toProblem()
                .asSolution());
        return functionCallRun(subject, context).setResult(result);
    }

}
