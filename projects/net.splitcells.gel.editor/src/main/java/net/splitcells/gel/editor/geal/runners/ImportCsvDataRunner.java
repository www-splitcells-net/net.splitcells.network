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
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.rating.rater.lib.HasSize.HAS_SIZE_NAME;
import static net.splitcells.website.Format.TEXT_PLAIN;

/**
 * In the past the CSV import and the data loading was done via 2 distinct commands.
 * This was moved into one, in order to provide sane default CSV content for the given data name,
 * when no CSV content is set yet.
 * By default, the data contains the header of the subject's table.
 */
public class ImportCsvDataRunner implements FunctionCallRunner {
    public static ImportCsvDataRunner importCsvDataRunner() {
        return new ImportCsvDataRunner();
    }

    private static final String IMPORT_CSV_DATA = "importCsvData";

    private ImportCsvDataRunner() {

    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals("importCsvData")) {
            return run;
        }
        try (val fcr = context.functionCallRecord(subject, functionCall, IMPORT_CSV_DATA, 1)) {
            final Table tableSubject = fcr.parseSubject(Table.class, subject);
            fcr.requireArgumentCount(1);
            final var dataName = fcr.parseArgument(String.class, 0);
            final var csvData = context.loadData(TEXT_PLAIN, dataName);
            if (csvData.getContent().length == 0) {
                csvData.setContent(StringUtils.toBytes(tableSubject.simplifiedHeaderCsv()));
            }
            tableSubject.withAddedCsv(parseString(csvData.getContent()));
            run.setResult(Optional.of(tableSubject));
            return run;
        }
    }
}
