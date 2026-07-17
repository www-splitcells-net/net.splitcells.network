/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Consumer;

import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class CsvDocument {
    public static CsvDocument csvDocument(String content, String... header) {
        return new CsvDocument(content, header);
    }

    private final CSVParser parser;

    private CsvDocument(String content, String... header) {
        final var format = CSVFormat.DEFAULT.builder()
                .setSkipHeaderRecord(true)
                .setHeader(header)
                .build();
        try {
            parser = format.parse(new StringReader(content));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    public CsvDocument process(Consumer<CsvRow> processor) {
        parser.getRecords().stream().map(CsvRow::csvRow).forEach(processor::accept);
        return this;
    }

}
