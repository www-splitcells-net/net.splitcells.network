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
package net.splitcells.gel.editor;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class FunctionCallMetaExecutor implements FunctionCallExecutor {
    public static FunctionCallMetaExecutor functionCallExecutor(Editor context
            , Optional<Object> subject) {
        return new FunctionCallMetaExecutor(context, subject);
    }

    private final Editor context;
    private final Optional<Object> subject;
    private final List<FunctionCallExecutor> executors = list();

    private FunctionCallMetaExecutor(Editor argContext, Optional<Object> argSubject) {
        context = argContext;
        subject = argSubject;
    }

    public FunctionCallMetaExecutor registerExecutor(FunctionCallExecutor executor) {
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
                .filter(e -> e.supports(functionCall))
                .findFirst();
        if (fittingExecutor.isEmpty()) {
            throw execException(tree("Unsupported function call.")
                    .withProperty("function call", functionCall.getSourceCodeQuote().toString()));
        }
        return fittingExecutor.get().execute(functionCall);
    }
}
