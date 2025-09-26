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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.editor.geal.runners.AttributeCallRunner.attributeCallRunner;
import static net.splitcells.gel.editor.geal.runners.ForAllCombsCallRunner.forAllCombsCallRunner;
import static net.splitcells.gel.editor.geal.runners.ForEachCallRunner.forEachCallRunner;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.HasSizeCallRunner.hasSizeCallRunner;
import static net.splitcells.gel.editor.geal.runners.ImportCsvDataRunner.importCsvDataRunner;
import static net.splitcells.gel.editor.geal.runners.OutputFormatCallRunner.outputFormatCallRunner;
import static net.splitcells.gel.editor.geal.runners.VariableResolutionRunner.variableResolutionRunner;
import static net.splitcells.gel.editor.geal.runners.SolutionCallRunner.solutionCallRunner;
import static net.splitcells.gel.editor.geal.runners.TableCallRunner.tableCallRunner;
import static net.splitcells.gel.editor.geal.runners.ThenCallRunner.thenCallRunner;

public class FunctionCallMetaExecutor implements FunctionCallExecutor {
    public static FunctionCallMetaExecutor child(FunctionCallRunner parent) {
        return functionCallMetaExecutor();
    }

    public static FunctionCallMetaExecutor child(FunctionCallRun parent) {
        return functionCallMetaExecutor();
    }

    public static FunctionCallMetaExecutor functionCallMetaExecutor() {
        return new FunctionCallMetaExecutor()
                .registerExecutor(attributeCallRunner())
                .registerExecutor(tableCallRunner())
                .registerExecutor(solutionCallRunner())
                .registerExecutor(forEachCallRunner())
                .registerExecutor(forAllCombsCallRunner())
                .registerExecutor(thenCallRunner())
                .registerExecutor(hasSizeCallRunner())
                .registerExecutor(importCsvDataRunner())
                .registerExecutor(outputFormatCallRunner())
                .registerExecutor(variableResolutionRunner())
                ;
    }

    private final List<FunctionCallRunner> executors = list();

    private FunctionCallMetaExecutor() {
    }

    public FunctionCallMetaExecutor registerExecutor(FunctionCallRunner executor) {
        executors.add(executor);
        return this;
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var fittingRun = executors.stream()
                .map(e -> e.execute(functionCall, subject, context))
                .filter(e -> e.getResult().isPresent())
                .findFirst();
        if (fittingRun.isEmpty()) {
            throw execException(tree("Unknown function call description.")
                    .withProperty("source code description", functionCall.getSourceCodeQuote().quote())
                    .withProperty("function call description", functionCall.toString()));
        }
        return fittingRun.get();
    }

}
