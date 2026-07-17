/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * @deprecated Use or migrate to {@link CsvDocument} instead.
 */
@JavaLegacy
public class CsvPrinter implements AutoCloseable {

    public static CsvPrinter csvDocument(List<String> header) {
        return new CsvPrinter(header);
    }

    public static String toCsvString(List<List<String>> content) {
        final var csvDocument = csvDocument(content.get(0));
        range(1, content.size()).forEach(i -> csvDocument.addLine(content.get(i)));
        return csvDocument.toString();
    }

    private final StringBuilder result = new StringBuilder();
    private final CSVPrinter printer;

    private CsvPrinter(List<String> header) {
        try {
            printer = new CSVPrinter(result, CSVFormat.RFC4180.withHeader(header.toArray(new String[header.size()])));
        } catch (IOException e) {
            throw execException(e);
        }
    }

    public void addLine(List<String> line) {
        try {
            printer.printRecord(line);
        } catch (IOException e) {
            throw execException(e);
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
