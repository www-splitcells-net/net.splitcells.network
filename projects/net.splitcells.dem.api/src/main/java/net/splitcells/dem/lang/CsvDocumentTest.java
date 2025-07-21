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
