/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.lang;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.CsvDocument.csvDocument;

public class CsvDocumentTest {
    @UnitTest
    public void test() {
        final var testData = """
                a,b
                1,2
                """;
        final List<String> testResult = list();
        csvDocument(testData, "a", "b").process(row -> testResult.add(row.value("b")));
        testResult.requireEquals(list("2"));
    }
}
