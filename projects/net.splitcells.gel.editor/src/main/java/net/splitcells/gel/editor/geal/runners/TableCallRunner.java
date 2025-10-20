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
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallChainDesc;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.NameDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;

import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.editor.EditorParser.SOLUTION_FUNCTION;
import static net.splitcells.gel.editor.EditorParser.TABLE_FUNCTION;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class TableCallRunner implements FunctionCallRunner {
    public static TableCallRunner tableCallRunner() {
        return new TableCallRunner();
    }

    private TableCallRunner() {

    }

    private boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(TABLE_FUNCTION);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!supports(functionCall)) {
            return run;
        }
        try (val fcr = context.functionCallRecord(TABLE_FUNCTION, 1)) {
            fcr.requireArgumentMinimalCount(functionCall, 2);

            final String tableName = fcr.parseArgument(functionCall, String.class, 0);
            final List<Attribute<?>> attributes = list();
            IntStream.range(1, functionCall.getArguments().size()).forEach(i ->
                    attributes.add(fcr.parseAttributeArgument(functionCall, Object.class, i)));
            return run.setResult(Optional.of(table(tableName, context, attributes)));
        }
    }

}
