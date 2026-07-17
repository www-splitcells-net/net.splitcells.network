/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.apache.commons.csv.CSVRecord;

@JavaLegacy
public class CsvRow {
    public static CsvRow csvRow(CSVRecord argRecord) {
        return new CsvRow(argRecord);
    }

    private final CSVRecord csvRecord;

    private CsvRow(CSVRecord argRecord) {
        csvRecord = argRecord;
    }

    public String value(String attribute) {
        return csvRecord.get(attribute);
    }

    public String value(int index) {
        return csvRecord.get(index);
    }
}
