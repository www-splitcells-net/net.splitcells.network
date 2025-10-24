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
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.EditorData;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;
import net.splitcells.gel.editor.geal.lang.StringDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.parseString;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser.functionCallRunnerParser;
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

    private static class Args {
        Table tableSubject;
        String dataName;
    }

    private static final FunctionCallRunnerParser<Args> PARSER = functionCallRunnerParser(IMPORT_CSV_DATA, 1
            , fcr -> {
                val args = new Args();
                args.tableSubject = fcr.parseSubject(Table.class);
                fcr.requireArgumentCount(1);
                args.dataName = fcr.parseArgument(String.class, 0);
                return args;
            });

    private ImportCsvDataRunner() {

    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        if (!functionCall.getName().getValue().equals(IMPORT_CSV_DATA)) {
            return run;
        }
        val args = PARSER.parse(subject, context, functionCall);
        final var csvData = context.loadData(TEXT_PLAIN, args.dataName);
        if (csvData.getContent().length == 0) {
            csvData.setContent(StringUtils.toBytes(args.tableSubject.simplifiedHeaderCsv()));
        }
        args.tableSubject.withAddedCsv(parseString(csvData.getContent()));
        run.setResult(Optional.of(args.tableSubject));
        return run;
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list(PARSER);
    }
}
