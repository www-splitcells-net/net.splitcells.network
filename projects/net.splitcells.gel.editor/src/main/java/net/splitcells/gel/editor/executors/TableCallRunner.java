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
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;
import net.splitcells.gel.editor.lang.geal.NameDesc;
import net.splitcells.gel.editor.lang.geal.StringDesc;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.editor.EditorParser.TABLE_FUNCTION;
import static net.splitcells.gel.editor.executors.FunctionCallRun.functionCallRun;

public class TableCallRunner implements FunctionCallRunner {
    public static TableCallRunner tableCallRunner() {
        return new TableCallRunner();
    }

    @Getter @Setter private Optional<Editor> context = Optional.empty();
    @Setter private Optional<Object> subject = Optional.empty();
    @Getter private Optional<Object> result = Optional.empty();

    private TableCallRunner() {

    }

    @Override
    public boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(TABLE_FUNCTION);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall)) {
            return run;
        }
        if (functionCall.getArguments().size() < 3) {
            throw execException("The table function requires at least 2 arguments, but " + functionCall.getArguments().size() + " were given.");
        }
        final var first = functionCall.getArguments().get(0).getExpression();
        final String tableName;
        switch (first) {
            case StringDesc n -> tableName = n.getValue();
            default ->
                    throw execException("The first argument has to be the table name represented by a string, but is a " + first.getClass() + " was given.");
        }
        final List<Attribute<?>> attributes = list();
        IntStream.range(1, functionCall.getArguments().size()).forEach(i -> {
            final Attribute<?> att;
            switch (functionCall.getArguments().get(i).getExpression()) {
                case NameDesc n -> attributes.add(context.orElseThrow().getAttributes().get(n.getValue()));
                default -> throw execException("Argument after the first one has to be the attribute names, but is a "
                        + functionCall.getArguments().get(i).getClass()
                        + " was given.");
            }
        });
        result = Optional.of(table(tableName, context.orElseThrow(), attributes));
        return run.setResult(result);
    }

    @Override
    public FunctionCallRunner setSubject(Optional<Object> argSubject) {
        subject = argSubject;
        return this;
    }

    @Override
    public FunctionCallRunner setContext(Optional<Editor> argContext) {
        context = argContext;
        return this;
    }
}
