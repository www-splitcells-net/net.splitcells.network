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
package net.splitcells.gel.editor.executors;

import lombok.Getter;
import lombok.Setter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.executors.AttributeCallRunner.attributeCallRunner;
import static net.splitcells.gel.editor.executors.ConstraintCallRunners.*;
import static net.splitcells.gel.editor.executors.SolutionCallRunner.solutionCallRunner;
import static net.splitcells.gel.editor.executors.TableCallRunner.tableCallRunner;

public class FunctionCallMetaExecutor implements FunctionCallExecutor {
    public static FunctionCallMetaExecutor child(FunctionCallRunner parent) {
        final var child = functionCallMetaExecutor();
        child.setContext(parent.getContext());
        child.setSubject(parent.getResult());
        return child;
    }

    public static FunctionCallMetaExecutor child(FunctionCallRun parent) {
        final var child = functionCallMetaExecutor();
        child.setContext(parent.getContext());
        child.setSubject(parent.getResult());
        return child;
    }

    public static FunctionCallMetaExecutor functionCallMetaExecutor() {
        return new FunctionCallMetaExecutor()
                .registerExecutor(attributeCallRunner())
                .registerExecutor(tableCallRunner())
                .registerExecutor(solutionCallRunner())
                .registerExecutor(forEachCallRunner())
                .registerExecutor(forAllCombinationsCallRunner());
    }

    /**
     * TODO Make {@link #context} and {@link #subject} immutable
     * Make only the result attributes mutable.
     */
    @Getter @Setter private Optional<Editor> context = Optional.empty();
    @Getter @Setter private Optional<Object> subject = Optional.empty();
    @Getter private Optional<Object> result = Optional.empty();
    private final List<FunctionCallRunner> executors = list();

    private FunctionCallMetaExecutor() {
    }

    public FunctionCallMetaExecutor registerExecutor(FunctionCallRunner executor) {
        executors.add(executor);
        return this;
    }

    @Override
    public boolean supports(FunctionCallDesc functionCall) {
        return executors.stream().map(e -> e.supports(functionCall)).anyMatch(s -> s);
    }

    @Override
    public FunctionCallMetaExecutor execute(FunctionCallDesc functionCall) {
        final var fittingExecutor = executors.stream()
                .filter(e -> {
                    e.setSubject(subject).setContext(context);
                    return e.supports(functionCall);
                })
                .findFirst();
        if (fittingExecutor.isEmpty()) {
            throw execException(tree("Unsupported function call.")
                    .withProperty("function call", functionCall.getSourceCodeQuote().toString()));
        }
        final var run = fittingExecutor.get().execute(functionCall);
        final var nextExecutor = child(run);
        return nextExecutor;
    }

    @Override
    public FunctionCallExecutor setSubject(Optional<Object> argSubject) {
        subject = argSubject;
        return this;
    }

    @Override
    public FunctionCallExecutor setContext(Optional<Editor> argContext) {
        context = argContext;
        return this;
    }
}
