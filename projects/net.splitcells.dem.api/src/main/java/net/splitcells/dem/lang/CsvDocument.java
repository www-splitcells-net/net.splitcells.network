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
package net.splitcells.dem.lang;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public class CsvDocument implements AutoCloseable {

    public static CsvDocument csvDocument(List<String> header) {
        return new CsvDocument(header);
    }

    private final StringBuilder result = new StringBuilder();
    private final CSVPrinter printer;

    private CsvDocument(List<String> header) {
        try {
            printer = new CSVPrinter(result, CSVFormat.RFC4180.withHeader(header.toArray(new String[header.size()])));
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    public void addLine(List<String> line) {
        try {
            printer.printRecord(line);
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    @Override
    public String toString() {
        return result.toString();
    }

    @Override
    public void close() throws Exception {
        printer.close();
    }
}
