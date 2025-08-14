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

import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.EditorData;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;

public class ImportCsvRunner implements FunctionCallRunner {
    public static ImportCsvRunner importCsvRunner() {
        return new ImportCsvRunner();
    }

    private ImportCsvRunner() {

    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        final Table tableSubject;
        if (subject.orElseThrow() instanceof Table table) {
            tableSubject = table;
        } else {
            return run;
        }
        if (!functionCall.getName().getValue().equals("importCsv")) {
            return run;
        }
        if (functionCall.getArguments().size() != 1) {
            return run;
        }
        final var firstArg = context.parse(functionCall.getArguments().get(0));
        if (firstArg instanceof EditorData data) {
            tableSubject.withAddedCsv(StringUtils.parseString(data.getContent()));
            run.setResult(Optional.of(tableSubject));
        } else {
            throw execException();
        }
        return run;
    }
}
